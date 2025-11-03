package com.mini.ewallet.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Hash password
     * @param password
     * @return 
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    /**
     * Verify password
     * @param password
     * @param hashedPassword
     * @return 
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    
    /**
     * Validate password strength
     * @param password
     * @return 
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate PIN (6 digits)
     * @param pin
     * @return 
     */
    public static boolean isValidPin(String pin) {
        return pin != null && pin.matches("^\\d{6}$");
    }
}
