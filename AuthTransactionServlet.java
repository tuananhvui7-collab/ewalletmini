package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.UserDAO;
import com.mini.ewallet.model.User;
import com.mini.ewallet.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.UUID; // Thêm import này

/**
 * UC-AUTH: Xác thực giao dịch
 * Verify PIN trước khi thực hiện giao dịch (Transfer, Payment)
 */
@WebServlet("/api/auth/verify-transaction")
public class AuthTransactionServlet extends HttpServlet {
    
    private static final int MAX_PIN_ATTEMPTS = 3;
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            
            BufferedReader reader = req.getReader();
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            
            JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
            
            String pin = json.get("transactionPin").getAsString();
            
            HttpSession session = req.getSession();
            String pinAttemptKey = "pin_attempts_" + userId;
            
            // Get user
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findById(userId);
            
            if (user == null) {
                JsonResponse.sendError(res, 404, "User không tồn tại");
                return;
            }
            
            // E1: Verify PIN
            if (!PasswordUtil.verifyPassword(pin, user.getTransactionPin())) {
                Integer attempts = (Integer) session.getAttribute(pinAttemptKey);
                if (attempts == null) {
                    attempts = 0;
                }
                attempts++;
                
                // E1.1: PIN wrong > 3 times → Cancel transaction
                if (attempts >= MAX_PIN_ATTEMPTS) {
                    session.removeAttribute(pinAttemptKey);
                    JsonResponse.sendError(res, 400, "Sai PIN quá 3 lần, giao dịch đã bị hủy");
                    Logger.warn("Transaction cancelled - too many PIN attempts for user: " + userId);
                    return;
                }
                
                session.setAttribute(pinAttemptKey, attempts);
                JsonResponse.sendError(res, 400, "Mã PIN không chính xác");
                return;
            }
            
            // Success: Reset attempts
            session.removeAttribute(pinAttemptKey);
            
            // [SỬA] TẠO "VÉ" GIAO DỊCH DÙNG 1 LẦN
            String transactionTicket = UUID.randomUUID().toString();
            long expiryTime = System.currentTimeMillis() + 60000; // Vé có hạn 60 giây

            session.setAttribute("TXN_TICKET_" + userId, transactionTicket);
            session.setAttribute("TXN_TICKET_EXPIRY_" + userId, expiryTime);

            // Trả vé về cho frontend
            JsonObject response = JsonResponse.success("Xác thực thành công");
            JsonObject data = new JsonObject();
            data.addProperty("verified", true);
            data.addProperty("transactionTicket", transactionTicket); // Gửi vé về
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Transaction authenticated for user: " + userId);
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Auth transaction error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}