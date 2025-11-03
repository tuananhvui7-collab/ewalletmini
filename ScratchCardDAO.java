// ============= SCRATCH CARD DAO =============
package com.mini.ewallet.dao;

import com.mini.ewallet.model.ScratchCard;
import com.mini.ewallet.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScratchCardDAO {
    
    /**
     * [MỚI] Create scratch card
     * @param card
     * @return 
     */
    public boolean create(ScratchCard card) {
        String sql = "INSERT INTO scratch_cards (serial_number, card_code, denomination, status, expiry_date, user_id, used_at) " +
                     "VALUES (?, ?, ?, ?, ?, NULL, NULL)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, card.getSerialNumber());
            stmt.setString(2, card.getCardCode());
            stmt.setBigDecimal(3, card.getDenomination());
            stmt.setString(4, card.getStatus());
            stmt.setDate(5, card.getExpiryDate());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Create card error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find card by serial and code
     * @param serial
     * @param code
     * @return 
     */
    public ScratchCard findBySerialAndCode(String serial, String code) {
        String sql = "SELECT * FROM scratch_cards WHERE serial_number = ? AND card_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serial);
            stmt.setString(2, code);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCard(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find card error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Update card status
     * @param serial
     * @param status
     * @param userId
     * @return 
     */
    public boolean updateCardStatus(String serial, String status, Long userId) {
        String sql = "UPDATE scratch_cards SET status = ?, user_id = ?, used_at = NOW() WHERE serial_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setObject(2, userId);
            stmt.setString(3, serial);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update card error: " + e.getMessage());
            return false;
        }
    }
    
    private ScratchCard mapResultSetToCard(ResultSet rs) throws SQLException {
        ScratchCard card = new ScratchCard();
        card.setId(rs.getLong("id"));
        card.setSerialNumber(rs.getString("serial_number"));
        card.setCardCode(rs.getString("card_code"));
        card.setDenomination(rs.getBigDecimal("denomination"));
        card.setStatus(rs.getString("status"));
        card.setExpiryDate(rs.getDate("expiry_date"));
        return card;
    }
}