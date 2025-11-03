// ============= JWT UTILITY =============
package com.mini.ewallet.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    
    private static final String SECRET = "your-super-secret-key-change-in-production-2024";
    private static final long EXPIRATION = 86400000; // 24 hours
    
    // Tạo SecretKey từ string
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Generate JWT Token
     * @param userId
     * @param phoneNumber
     */
    public static String generateToken(Long userId, String phoneNumber) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("phoneNumber", phoneNumber)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Validate Token
     * @param token
     * @return 
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("❌ Invalid token: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get User ID from Token
     * @param token
     * @return 
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * Get Phone Number from Token
     * @param token
     * @return 
     */
    public static String getPhoneNumberFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("phoneNumber", String.class);
        } catch (JwtException e) {
            return null;
        }
    }
}