// ============= USER MODEL =============
package com.mini.ewallet.model;

import java.sql.Timestamp;

public class User {
    private Long id;
    private String phoneNumber;
    private String email;
    private String fullName;
    private String password;
    private String transactionPin;
    private String role; // USER, MERCHANT, ADMIN
    private String status; // ACTIVE, LOCKED
    private Timestamp createdAt;

    // Constructors
    public User() {}

    public User(String phoneNumber, String email, String fullName, String password, String transactionPin, String role, String status) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.transactionPin = transactionPin;
        this.role = role;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTransactionPin() { return transactionPin; }
    public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}