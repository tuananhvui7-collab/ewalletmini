// ============= VALIDATION UTILITY =============
package com.mini.ewallet.util;

public class ValidationUtil {
    
    /**
     * Validate phone number
     * @param phone
     * @return 
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^0\\d{9,10}$");
    }
    
    /**
     * Validate email
     * @param email
     * @return 
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Validate name
     * @param name
     * @return 
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2;
    }
    
    /**
     * Validate amount
     * @param amountStr
     * @return 
     */
    public static boolean isValidAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}