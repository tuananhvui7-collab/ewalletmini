// ============= TRANSACTION DAO =============
package com.mini.ewallet.dao;

import com.mini.ewallet.model.Transaction;
import com.mini.ewallet.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    /**
     * Create transaction record
     * @param transaction
     * @return 
     */
    public boolean create(Transaction transaction) {
        String sql = "INSERT INTO transactions (type, from_user_id, to_user_id, amount, status, description, reference_code) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transaction.getType());
            stmt.setObject(2, transaction.getFromUserId());
            stmt.setLong(3, transaction.getToUserId());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getStatus());
            stmt.setString(6, transaction.getDescription());
            stmt.setString(7, transaction.getReferenceCode());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Create transaction error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user transaction history
     * @param userId
     * @param limit
     * @return 
     */
    public List<Transaction> getUserHistory(Long userId, int limit) {
        String sql = "SELECT * FROM transactions WHERE from_user_id = ? OR to_user_id = ? " +
                     "ORDER BY created_at DESC LIMIT ?";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Get history error: " + e.getMessage());
        }
        return transactions;
    }
    
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction txn = new Transaction();
        txn.setId(rs.getLong("id"));
        txn.setType(rs.getString("type"));
        txn.setFromUserId(rs.getObject("from_user_id") != null ? rs.getLong("from_user_id") : null);
        txn.setToUserId(rs.getLong("to_user_id"));
        txn.setAmount(rs.getBigDecimal("amount"));
        txn.setStatus(rs.getString("status"));
        txn.setDescription(rs.getString("description"));
        txn.setReferenceCode(rs.getString("reference_code"));
        txn.setCreatedAt(rs.getTimestamp("created_at"));
        return txn;
    }
}
