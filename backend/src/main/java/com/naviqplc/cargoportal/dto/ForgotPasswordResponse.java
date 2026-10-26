package com.naviqplc.cargoportal.dto;

// Answer of "Send OTP":  { "message": "...", "demoOtp": "482913" }
// demoOtp is filled only in screen (demo) mode, otherwise it is null
public class ForgotPasswordResponse {

    private String message;
    private String demoOtp;

    public ForgotPasswordResponse(String message, String demoOtp) {
        this.message = message;
        this.demoOtp = demoOtp;
    }

    public String getMessage() {
        return message;
    }

    public String getDemoOtp() {
        return demoOtp;
    }
}