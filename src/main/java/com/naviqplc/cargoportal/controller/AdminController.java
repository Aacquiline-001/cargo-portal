package com.naviqplc.cargoportal.controller;

import com.naviqplc.cargoportal.dto.AdminRequest;
import com.naviqplc.cargoportal.dto.AdminResponse;
import com.naviqplc.cargoportal.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET /api/admins                      → all admins
    // GET /api/admins?region=India         → search
    @GetMapping
    public List<AdminResponse> searchAdmins(
            @RequestParam(required = false) String adminId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String employeeNumber) {
        return adminService.searchAdmins(adminId, region, employeeNumber);
    }

    // GET /api/admins/5                    → one admin
    @GetMapping("/{adminPk}")
    public AdminResponse getAdminById(@PathVariable Integer adminPk) {
        return adminService.getAdminById(adminPk);
    }

    // POST /api/admins                     → create
    @PostMapping
    public AdminResponse createAdmin(@Valid @RequestBody AdminRequest request) {
        return adminService.createAdmin(request);
    }

    // PUT /api/admins/5                    → update
    @PutMapping("/{adminPk}")
    public AdminResponse updateAdmin(@PathVariable Integer adminPk,
                                     @Valid @RequestBody AdminRequest request) {
        return adminService.updateAdmin(adminPk, request);
    }

    // DELETE /api/admins/5                 → delete
    @DeleteMapping("/{adminPk}")
    public String deleteAdmin(@PathVariable Integer adminPk) {
        adminService.deleteAdmin(adminPk);
        return "Admin deleted successfully";
    }
}