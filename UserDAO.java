// ============= USER DAO =============
package com.mini.ewallet.dao;

import com.mini.ewallet.model.User;
import com.mini.ewallet.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*import java.util.List;*/
public class UserDAO {
    
    /**
     * Register new user
     * @param user
     * @return 
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (phone_number, email, full_name, password, transaction_pin, role, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getPhoneNumber());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getTransactionPin());
            stmt.setString(6, user.getRole());
            stmt.setString(7, user.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Register error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find user by phone number
     * @param phoneNumber
     * @return 
     */
    public User findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find user error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Find user by ID
     * @param id
     * @return 
     */
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find user error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Update user
     * @param user
     * @return 
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getStatus());
            stmt.setLong(3, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update user error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update password
     * @param userId
     * @param newPassword
     * @return 
     */
    public boolean updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setLong(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update password error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update PIN
     * @param userId
     * @param newPin
     * @return 
     */
    public boolean updatePin(Long userId, String newPin) {
        String sql = "UPDATE users SET transaction_pin = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPin);
            stmt.setLong(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Update PIN error: " + e.getMessage());
            return false;
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPassword(rs.getString("password"));
        user.setTransactionPin(rs.getString("transaction_pin"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    /**
     * [SỬA LẠI HÀM NÀY]
     * Find user by email
     * @param email
     * @return 
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Find user error: " + e.getMessage());
        }
        return null;
    }
}