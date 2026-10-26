package com.naviqplc.cargoportal.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="user_login")
//@Getter @Setter
//@NoArgsConstructor
public class UserLogin {
    @Id //primary key
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id")
    private int userId;

    @Column(name="username",nullable=false,unique=true,length=100)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email_id", nullable = false, unique = true, length = 150)
    private String emailId;

    @Enumerated(EnumType.STRING)
    @Column(name="role",nullable = false,length=50)
    private Role role;

    @Column(name="is_active",nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(name="created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "must_change_password")
    private Boolean mustChangePassword;     // true → must choose a new password at next sign-in

    public UserLogin()
    {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
