package com.naviqplc.cargoportal.entity;

import jakarta.persistence.*;
//import lombok.*;
import org.apache.catalina.User;

@Entity
@Table(name="business_admin")
//@Getter @Setter
//@NoArgsConstructor
public class BusinessAdmin {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="admin_pk")
    private int adminPk;

    @OneToOne(cascade=CascadeType.ALL,fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name="user_id",nullable = false,unique = true)
    private UserLogin user;

    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @Column(name = "branch", nullable = false, length = 100)
    private String branch;

    @Column(name = "admin_id", nullable = false, unique = true, length = 50)
    private String adminId;

    @Column(name = "admin_name", nullable = false, length = 150)
    private String adminName;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "employee_number", unique = true, length = 50)
    private String employeeNumber;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    public BusinessAdmin() {}

    public int getAdminPk() {
        return adminPk;
    }

    public void setAdminPk(int adminPk) {
        this.adminPk = adminPk;
    }

    public UserLogin getUser() {
        return user;
    }

    public void setUser(UserLogin user) {
        this.user = user;
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
}
