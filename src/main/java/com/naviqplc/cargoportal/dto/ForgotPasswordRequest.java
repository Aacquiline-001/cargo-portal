package com.naviqplc.cargoportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// What the "Forgot password" form sends:  { "username": "Agent50", "emailId": "jissy@gmail.com" }
public class ForgotPasswordRequest {

    @NotBlank(message = "User ID is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String emailId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}