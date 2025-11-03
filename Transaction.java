// ============= TRANSACTION MODEL =============
package com.mini.ewallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private Long id;
    private String type; // TOPUP, P2P, PAYMENT
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private String status; // PENDING, COMPLETED, FAILED
    private String description;
    private String referenceCode;
    private Timestamp createdAt;

    public Transaction() {}

    public Transaction(String type, Long fromUserId, Long toUserId, BigDecimal amount, String status, String description) {
        this.type = type;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.status = status;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }

    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
