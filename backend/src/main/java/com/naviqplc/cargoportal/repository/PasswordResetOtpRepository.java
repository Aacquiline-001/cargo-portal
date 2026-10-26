package com.naviqplc.cargoportal.repository;

import com.naviqplc.cargoportal.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Integer> {

    // the NEWEST OTP of this user that hasn't been used yet
    Optional<PasswordResetOtp> findFirstByUserUserIdAndUsedFalseOrderByCreatedAtDesc(Integer userId);

    // ALL unused OTPs of this user (to cancel old ones when a new OTP is sent)
    List<PasswordResetOtp> findByUserUserIdAndUsedFalse(Integer userId);
}