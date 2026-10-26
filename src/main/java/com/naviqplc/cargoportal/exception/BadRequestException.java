package com.naviqplc.cargoportal.exception;

// "Your request is wrong" — e.g. wrong current password, request already handled → 400
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}