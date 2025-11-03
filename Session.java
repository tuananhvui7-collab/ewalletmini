package com.mini.ewallet.model;

import java.sql.Timestamp;

public class Session {
    private Long id;
    private Long userId;
    private String token;
    private String userType; // USER, ADMIN
    private Timestamp createdAt;
    private Timestamp expiryAt;
    private String ipAddress;
    private String status; // ACTIVE, EXPIRED, REVOKED

    // Constructors
    public Session() {}

    public Session(Long userId, String token, String userType, Timestamp expiryAt) {
        this.userId = userId;
        this.token = token;
        this.userType = userType;
        this.expiryAt = expiryAt;
        this.status = "ACTIVE";
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getExpiryAt() { return expiryAt; }
    public void setExpiryAt(Timestamp expiryAt) { this.expiryAt = expiryAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
