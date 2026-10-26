package com.naviqplc.cargoportal.service;

import com.naviqplc.cargoportal.entity.UserLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Delivers the OTP to the user.
//   console → printed in the IntelliJ console only
//   screen  → printed AND sent back to the web page (demo mode)
//   (later: email)
@Service
public class OtpSender {

    private static final Logger log = LoggerFactory.getLogger(OtpSender.class);

    private String delivery;

    public OtpSender(@Value("${app.otp.delivery:console}") String delivery) {
        this.delivery = delivery;
    }

    // true → the controller may send the OTP back to the page
    public boolean showOnScreen() {
        return delivery.equals("screen");
    }

    public void sendOtp(UserLogin user, String otp, int validMinutes) {

        if (delivery.equals("console") || delivery.equals("screen")) {
            log.info("=================================================");
            log.info(" PASSWORD RESET OTP ({} mode)", delivery);
            log.info(" User ID : {}", user.getUsername());
            log.info(" Email   : {}", user.getEmailId());
            log.info(" OTP     : {}   (valid for {} minutes)", otp, validMinutes);
            log.info("=================================================");
            return;
        }

        log.warn("app.otp.delivery='{}' is not supported yet. Use 'console' or 'screen'.", delivery);
    }
}