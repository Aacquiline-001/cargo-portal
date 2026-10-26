package com.naviqplc.cargoportal.dto;

// What /api/auth/login sends back
public class LoginResponse {

    private String token;
    private String username;
    private String role;
    private Boolean mustChangePassword;   // true → the app shows "Choose your password" first

    public LoginResponse(String token, String username, String role, Boolean mustChangePassword) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.mustChangePassword = mustChangePassword;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }
}