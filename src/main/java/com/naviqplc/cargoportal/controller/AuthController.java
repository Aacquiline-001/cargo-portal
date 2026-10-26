package com.naviqplc.cargoportal.controller;

import com.naviqplc.cargoportal.dto.ForgotPasswordRequest;
import com.naviqplc.cargoportal.dto.LoginRequest;
import com.naviqplc.cargoportal.dto.LoginResponse;
import com.naviqplc.cargoportal.dto.MessageResponse;
import com.naviqplc.cargoportal.dto.ResetPasswordRequest;
import com.naviqplc.cargoportal.entity.UserLogin;
import com.naviqplc.cargoportal.repository.UserLoginRepository;
import com.naviqplc.cargoportal.security.JwtUtil;
import com.naviqplc.cargoportal.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.naviqplc.cargoportal.dto.ForgotPasswordResponse;
import com.naviqplc.cargoportal.service.OtpSender;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserLoginRepository userLoginRepository;
    private JwtUtil jwtUtil;
    private PasswordService passwordService;
    private OtpSender otpSender;

    public AuthController(AuthenticationManager authenticationManager,
                          UserLoginRepository userLoginRepository,
                          JwtUtil jwtUtil,
                          PasswordService passwordService,
                          OtpSender otpSender) {
        this.authenticationManager = authenticationManager;
        this.userLoginRepository = userLoginRepository;
        this.jwtUtil = jwtUtil;
        this.passwordService = passwordService;
        this.otpSender = otpSender;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        // 1. Check username + password (wrong → 401, inactive → 401)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 2. Load the user to know their role
        UserLogin user = userLoginRepository.findByUsername(request.getUsername()).get();

        // 3. Make the token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        // 4. Send it back (+ whether they must choose a new password first)
        return new LoginResponse(token, user.getUsername(), user.getRole().name(),
                user.getMustChangePassword());
    }

    // SCREEN 1 — POST /api/auth/forgot-password   { "username": "...", "emailId": "..." }
    // SCREEN 1 — POST /api/auth/forgot-password   { "username": "...", "emailId": "..." }
    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        String otp = passwordService.sendResetOtp(request.getUsername(), request.getEmailId());

        // demo mode only: send the code back so the page can show it
        String demoOtp = null;
        if (otpSender.showOnScreen()) {
            demoOtp = otp;                     // null if the details didn't match
        }

        return new ForgotPasswordResponse(
                "If the User ID and email match an account, an OTP has been sent", demoOtp);
    }

    // SCREEN 2 — POST /api/auth/reset-password   { "username": "...", "otp": "...", "newPassword": "..." }
    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPasswordWithOtp(request.getUsername(), request.getOtp(), request.getNewPassword());
        return new MessageResponse("Password reset. You can sign in with your new password.");
    }
}