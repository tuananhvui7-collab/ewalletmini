package com.mini.ewallet.dao;

import com.mini.ewallet.model.Session;
import com.mini.ewallet.util.DatabaseConnection;
import java.sql.*;

public class SessionDAO {
    
    /**
     * Create session
     * @param session
     * @return 
     */
    public boolean create(Session session) {
        String sql = "INSERT INTO sessions (user_id, token, user_type, expiry_at, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, session.getUserId());
            stmt.setString(2, session.getToken());
            stmt.setString(3, session.getUserType());
            stmt.setTimestamp(4, session.getExpiryAt());
            stmt.setString(5, session.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Create session error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find session by token
     * @param token
     * @return 
     */
    public Session findByToken(String token) {
        String sql = "SELECT * FROM sessions WHERE token = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find session error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Revoke session
     * @param token
     * @return 
     */
    public boolean revoke(String token) {
        String sql = "UPDATE sessions SET status = 'REVOKED' WHERE token = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Revoke session error: " + e.getMessage());
            return false;
        }
    }
    
    private Session mapResultSetToSession(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setId(rs.getLong("id"));
        session.setUserId(rs.getLong("user_id"));
        session.setToken(rs.getString("token"));
        session.setUserType(rs.getString("user_type"));
        session.setCreatedAt(rs.getTimestamp("created_at"));
        session.setExpiryAt(rs.getTimestamp("expiry_at"));
        session.setIpAddress(rs.getString("ip_address"));
        session.setStatus(rs.getString("status"));
        return session;
    }
}