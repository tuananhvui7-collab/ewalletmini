// ============= SCRATCH CARD MODEL =============
package com.mini.ewallet.model;

import java.math.BigDecimal;
import java.sql.Date;

public class ScratchCard {
    private Long id;
    private String serialNumber;
    private String cardCode;
    private BigDecimal denomination;
    private String status; // NEW, USED, EXPIRED
    private Date expiryDate;

    public ScratchCard() {}

    public ScratchCard(String serialNumber, String cardCode, BigDecimal denomination, String status, Date expiryDate) {
        this.serialNumber = serialNumber;
        this.cardCode = cardCode;
        this.denomination = denomination;
        this.status = status;
        this.expiryDate = expiryDate;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getCardCode() { return cardCode; }
    public void setCardCode(String cardCode) { this.cardCode = cardCode; }

    public BigDecimal getDenomination() { return denomination; }
    public void setDenomination(BigDecimal denomination) { this.denomination = denomination; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
}