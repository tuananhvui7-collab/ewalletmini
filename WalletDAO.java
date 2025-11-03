// ============= WALLET DAO =============
package com.mini.ewallet.dao;

import com.mini.ewallet.model.Wallet;
import com.mini.ewallet.util.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;

public class WalletDAO {
    
    /**
     * Create wallet for user
     * @param userId
     * @return 
     */
    public boolean create(Long userId) {
        String sql = "INSERT INTO wallets (user_id, balance) VALUES (?, 0)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Create wallet error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get wallet by user ID
     * @param userId
     * @return 
     */
    public Wallet getByUserId(Long userId) {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Wallet wallet = new Wallet();
                wallet.setId(rs.getLong("id"));
                wallet.setUserId(rs.getLong("user_id"));
                wallet.setBalance(rs.getBigDecimal("balance"));
                return wallet;
            }
        } catch (SQLException e) {
            System.err.println("❌ Get wallet error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * [SỬA] Trừ tiền (Atomic) - An toàn cho nhiều giao dịch cùng lúc
     * @param conn
     * @param userId
     * @param amount
     * @return 
     * @throws java.sql.SQLException 
     */
    public boolean debit(Connection conn, Long userId, BigDecimal amount) throws SQLException {
        // Câu lệnh UPDATE này sẽ tự động check số dư
        String sql = "UPDATE wallets SET balance = balance - ? WHERE user_id = ? AND balance >= ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, amount);
            stmt.setLong(2, userId);
            stmt.setBigDecimal(3, amount); // Check số dư
            
            return stmt.executeUpdate() > 0; // Nếu > 0, nghĩa là trừ thành công
        }
    }
    
    /**
     * [SỬA] Cộng tiền (Atomic) - An toàn cho nhiều giao dịch cùng lúc
     * @param conn
     * @param userId
     * @param amount
     * @return 
     * @throws java.sql.SQLException 
     */
    public boolean credit(Connection conn, Long userId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE wallets SET balance = balance + ? WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, amount);
            stmt.setLong(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    // HÀM updateBalance(Long userId, BigDecimal newBalance) ĐÃ BỊ XÓA
    // VÌ NÓ KHÔNG AN TOÀN
}