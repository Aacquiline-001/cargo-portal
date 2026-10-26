package com.naviqplc.cargoportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminRequest {

    @NotBlank(message = "Admin ID is required")
    private String adminId;

    @NotBlank(message = "Admin name is required")
    private String adminName;

    @NotBlank(message = "Region is required")
    private String region;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Employee number is required")
    private String employeeNumber;

    private String contactNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String emailId;

    private Boolean active;

    // getters and setters

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}