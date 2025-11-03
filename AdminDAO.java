package com.mini.ewallet.dao;

import com.mini.ewallet.model.Admin;
import com.mini.ewallet.util.DatabaseConnection;
import java.sql.*;

public class AdminDAO {
    
    /**
     * Find admin by username
     * @param username
     * @return 
     */
    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAdmin(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find admin error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Find admin by ID
     * @param id
     * @return 
     */
    public Admin findById(Long id) {
        String sql = "SELECT * FROM admins WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAdmin(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find admin error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Create admin
     * @param admin
     * @return 
     */
    public boolean create(Admin admin) {
        String sql = "INSERT INTO admins (username, password, full_name, role, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getFullName());
            stmt.setString(4, admin.getRole());
            stmt.setString(5, admin.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Create admin error: " + e.getMessage());
            return false;
        }
    }
    
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getLong("id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setFullName(rs.getString("full_name"));
        admin.setRole(rs.getString("role"));
        admin.setStatus(rs.getString("status"));
        admin.setCreatedAt(rs.getTimestamp("created_at"));
        return admin;
    }
}
