// ============= MERCHANT DAO =============
package com.mini.ewallet.dao;

import com.mini.ewallet.model.Merchant;
import com.mini.ewallet.util.DatabaseConnection;
import java.sql.*;

public class MerchantDAO {
    
    /**
     * Find merchant by QR code
     * @param qrCode
     * @return 
     */
    public Merchant findByQRCode(String qrCode) {
        String sql = "SELECT * FROM merchant_qr_codes WHERE qr_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, qrCode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Merchant merchant = new Merchant();
                merchant.setId(rs.getLong("id"));
                merchant.setMerchantId(rs.getLong("merchant_id"));
                merchant.setQrCode(rs.getString("qr_code"));
                return merchant;
            }
        } catch (SQLException e) {
            System.err.println("❌ Find merchant error: " + e.getMessage());
        }
        return null;
    }
}