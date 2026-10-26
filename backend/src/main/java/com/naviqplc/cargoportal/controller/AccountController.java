package com.naviqplc.cargoportal.controller;

import com.naviqplc.cargoportal.dto.ChangePasswordRequest;
import com.naviqplc.cargoportal.dto.MessageResponse;
import com.naviqplc.cargoportal.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

// Things a signed-in user does to THEIR OWN account
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private PasswordService passwordService;

    public AccountController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // POST /api/account/change-password   (admins and agents)
    @PostMapping("/change-password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                          Principal principal) {
        passwordService.changePassword(principal.getName(),
                request.getCurrentPassword(), request.getNewPassword());
        return new MessageResponse("Password changed");
    }
}