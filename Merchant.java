// ============= MERCHANT QR CODE MODEL =============
package com.mini.ewallet.model;

import java.sql.Timestamp;

public class Merchant {
    private Long id;
    private Long merchantId;
    private String qrCode;
    private String description;
    private Timestamp createdAt;

    public Merchant() {}

    public Merchant(Long merchantId, String qrCode) {
        this.merchantId = merchantId;
        this.qrCode = qrCode;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}