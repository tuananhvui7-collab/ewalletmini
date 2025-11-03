// ============= DATABASE CONNECTION =============
package com.mini.ewallet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mini_ewallet_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // ⚠️ CHANGE THIS
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found!", e);
        }
    }
    
    /**
     * Get database connection
     * @return 
     * @rootthrows java.sql.SQLException 
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test connection
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
