// ============= CONSTANTS =============
package com.mini.ewallet.util;

public class Constants {
    
    // HTTP Status
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_SERVER_ERROR = 500;
    
    // User Role
    public static final String ROLE_USER = "USER";
    public static final String ROLE_MERCHANT = "MERCHANT";
    public static final String ROLE_ADMIN = "ADMIN";
    
    // User Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_LOCKED = "LOCKED";
    
    // Transaction Type
    public static final String TXN_TOPUP = "TOPUP";
    public static final String TXN_P2P = "P2P";
    public static final String TXN_PAYMENT = "PAYMENT";

    /**
     * k biet viet gi?
     */
    public static final String TXN_ADMERCHANT = "CASHOUT";
    
    // Transaction Status
    public static final String TXN_STATUS_PENDING = "PENDING";
    public static final String TXN_STATUS_COMPLETED = "COMPLETED";
    public static final String TXN_STATUS_FAILED = "FAILED";
    
    // Card Status
    public static final String CARD_NEW = "NEW";
    public static final String CARD_USED = "USED";
    public static final String CARD_EXPIRED = "EXPIRED";
    
    // Session Keys
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_TOKEN = "token";
    public static final String SESSION_PHONE = "phone";
    public static final String SESSION_ROLE = "role";
}