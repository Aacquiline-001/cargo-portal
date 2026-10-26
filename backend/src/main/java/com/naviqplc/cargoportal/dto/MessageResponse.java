package com.naviqplc.cargoportal.dto;

// A simple answer:  { "message": "Password changed" }
public class MessageResponse {

    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}