package com.mini.ewallet.model;

import java.sql.Timestamp;

public class Admin {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String role; // SUPER_ADMIN, OPERATOR
    private String status; // ACTIVE, LOCKED
    private Timestamp createdAt;

    // Constructors
    public Admin() {}

    public Admin(String username, String password, String fullName, String role, String status) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}