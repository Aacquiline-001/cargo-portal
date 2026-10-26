package com.naviqplc.cargoportal.dto;

import jakarta.validation.constraints.*;

public class AgentRequest {
    @NotBlank(message = "User ID is required")
    @Size(max = 100)
    private String loginId;

    @NotBlank(message = "User name is required")
    @Size(max = 150)
    private String userName;

    @NotBlank(message = "Language is required")
    @Size(max = 50)
    private String language;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(max = 150)
    private String emailId;

    @NotBlank(message = "User type is required")
    @Size(max = 100)
    private String userType;

    @Size(max = 100)
    private String privilege;

    @NotBlank(message = "Agent code is required")
    @Size(max = 50)
    private String agentCode;

    @Size(max = 200)
    private String companyName;

    private String address;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9+\\- ]*$",
            message = "Telephone can only have digits, +, - and spaces")
    private String telephone;

    private Boolean active;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
