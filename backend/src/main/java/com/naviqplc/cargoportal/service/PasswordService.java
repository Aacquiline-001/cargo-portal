package com.naviqplc.cargoportal.service;

import com.naviqplc.cargoportal.entity.PasswordResetOtp;
import com.naviqplc.cargoportal.entity.UserLogin;
import com.naviqplc.cargoportal.exception.BadRequestException;
import com.naviqplc.cargoportal.exception.ResourceNotFoundException;
import com.naviqplc.cargoportal.repository.PasswordResetOtpRepository;
import com.naviqplc.cargoportal.repository.UserLoginRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordService {

    // ---- OTP rules (all in one place) ----
    private static final int OTP_VALID_MINUTES = 10;   // code works for 10 minutes
    private static final int MAX_ATTEMPTS = 5;         // 5 wrong tries → code blocked
    private static final int WAIT_SECONDS = 60;        // 1 minute between two "Send OTP"

    private UserLoginRepository userLoginRepository;
    private PasswordResetOtpRepository otpRepository;
    private PasswordEncoder passwordEncoder;
    private OtpSender otpSender;
    private SecureRandom random = new SecureRandom();

    public PasswordService(UserLoginRepository userLoginRepository,
                           PasswordResetOtpRepository otpRepository,
                           PasswordEncoder passwordEncoder,
                           OtpSender otpSender) {
        this.userLoginRepository = userLoginRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpSender = otpSender;
    }

    // =====================================================================
    // SCREEN 1: "Send OTP"  (User ID + email)
    // =====================================================================
    @Transactional
    public String sendResetOtp(String username, String emailId) {

        // 1. find the user; if anything is wrong, stop quietly
        //    (the screen always says the same thing, so nobody can test which IDs exist)
        Optional<UserLogin> result = userLoginRepository.findByUsername(username.trim());
        if (result.isEmpty()) {
            return null;
        }
        UserLogin user = result.get();

        if (!user.getEmailId().equalsIgnoreCase(emailId.trim())) {
            return null;                                  // email doesn't belong to this user
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            return null;                                  // inactive users can't sign in anyway
        }

        // 2. don't allow "Send OTP" again and again
        Optional<PasswordResetOtp> latest =
                otpRepository.findFirstByUserUserIdAndUsedFalseOrderByCreatedAtDesc(user.getUserId());
        if (latest.isPresent()) {
            LocalDateTime allowedAgainAt = latest.get().getCreatedAt().plusSeconds(WAIT_SECONDS);
            if (LocalDateTime.now().isBefore(allowedAgainAt)) {
                throw new BadRequestException("Please wait a minute before asking for a new OTP");
            }
        }

        // 3. cancel older codes, so only the newest one works
        List<PasswordResetOtp> oldOtps = otpRepository.findByUserUserIdAndUsedFalse(user.getUserId());
        for (PasswordResetOtp old : oldOtps) {
            old.setUsed(true);
        }
        otpRepository.saveAll(oldOtps);

        // 4. make a new 6-digit code and save only its HASH
        String otp = generateOtp();

        PasswordResetOtp newOtp = new PasswordResetOtp();
        newOtp.setUser(user);
        newOtp.setOtpHash(passwordEncoder.encode(otp));
        newOtp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES));
        newOtp.setAttempts(0);
        newOtp.setUsed(false);
        otpRepository.save(newOtp);

        // 5. deliver it (console today, email later)
        otpSender.sendOtp(user, otp, OTP_VALID_MINUTES);
        return otp;
    }

    // =====================================================================
    // SCREEN 2: "Reset password"  (OTP + new password)
    // noRollbackFor: keep the "wrong attempt +1" even though we throw an error
    // =====================================================================
    @Transactional(noRollbackFor = BadRequestException.class)
    public void resetPasswordWithOtp(String username, String otp, String newPassword) {

        // 1. the user and their newest unused OTP
        Optional<UserLogin> userResult = userLoginRepository.findByUsername(username.trim());
        if (userResult.isEmpty()) {
            throw new BadRequestException("Invalid or expired OTP");
        }
        UserLogin user = userResult.get();

        Optional<PasswordResetOtp> otpResult =
                otpRepository.findFirstByUserUserIdAndUsedFalseOrderByCreatedAtDesc(user.getUserId());
        if (otpResult.isEmpty()) {
            throw new BadRequestException("Invalid or expired OTP");
        }
        PasswordResetOtp savedOtp = otpResult.get();

        // 2. still valid?
        if (savedOtp.isExpired()) {
            throw new BadRequestException("This OTP has expired. Please request a new one.");
        }
        if (savedOtp.getAttempts() >= MAX_ATTEMPTS) {
            throw new BadRequestException("Too many wrong attempts. Please request a new OTP.");
        }

        // 3. wrong code → count the attempt
        if (!passwordEncoder.matches(otp, savedOtp.getOtpHash())) {
            int attempts = savedOtp.getAttempts() + 1;
            savedOtp.setAttempts(attempts);
            otpRepository.save(savedOtp);

            int triesLeft = MAX_ATTEMPTS - attempts;
            if (triesLeft <= 0) {
                throw new BadRequestException("Too many wrong attempts. Please request a new OTP.");
            }
            throw new BadRequestException("Incorrect OTP. " + triesLeft + " tries left.");
        }

        // 4. correct! set the new password, use up the code
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);           // they chose their own password
        userLoginRepository.save(user);

        savedOtp.setUsed(true);                      // the same code can't be used twice
        otpRepository.save(savedOtp);
    }

    // =====================================================================
    // CHANGE PASSWORD (signed-in users) — same as before
    // =====================================================================
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {

        Optional<UserLogin> result = userLoginRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("User not found: " + username);
        }
        UserLogin user = result.get();

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BadRequestException("New password must be different from the current one");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userLoginRepository.save(user);
    }

    // ---------- helper ----------

    // random number 0 – 999999, always shown with 6 digits (e.g. 7 → "000007")
    private String generateOtp() {
        int number = random.nextInt(1000000);
        return String.format("%06d", number);
    }
}