// ============= WALLET MODEL =============
package com.mini.ewallet.model;

import java.math.BigDecimal;

public class Wallet {
    private Long id;
    private Long userId;
    private BigDecimal balance;

    public Wallet() {}

    public Wallet(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}