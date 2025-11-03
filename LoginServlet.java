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

/**
 *
 * @author tuananh
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            BufferedReader reader = req.getReader();
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            
            JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
            
            String phone = json.get("phoneNumber").getAsString();
            String password = json.get("password").getAsString();
            
            HttpSession session = req.getSession();
            String attemptKey = "login_attempts_" + phone;
            String lockTimeKey = "login_lock_time_" + phone;
            
            // Check if locked
            Long lockTime = (Long) session.getAttribute(lockTimeKey);
            if (lockTime != null && (System.currentTimeMillis() - lockTime) < LOCK_DURATION) {
                JsonResponse.sendError(res, 400, "Tài khoản tạm khóa. Vui lòng thử lại sau 15 phút");
                return;
            }
            
            // Find user
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByPhoneNumber(phone);
            
            if (user == null) {
                incrementAttempts(session, phone, attemptKey, lockTimeKey);
                JsonResponse.sendError(res, 400, "Số điện thoại hoặc mật khẩu không đúng");
                return;
            }
            
            // E2: Check if account locked
            if (!user.getStatus().equals(Constants.STATUS_ACTIVE)) {
                JsonResponse.sendError(res, 400, "Tài khoản của bạn đã bị tạm khóa");
                return;
            }
            
            // Verify password
            if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                incrementAttempts(session, phone, attemptKey, lockTimeKey);
                JsonResponse.sendError(res, 400, "Số điện thoại hoặc mật khẩu không đúng");
                return;
            }
            
            // Success - reset attempts
            session.removeAttribute(attemptKey);
            session.removeAttribute(lockTimeKey);
            
            // Generate token
            String token = JwtUtil.generateToken(user.getId(), phone);
            
            JsonObject response = JsonResponse.success("Đăng nhập thành công");
            JsonObject data = new JsonObject();
            data.addProperty("userId", user.getId());
            data.addProperty("phoneNumber", phone);
            data.addProperty("fullName", user.getFullName());
            data.addProperty("token", token);
            data.addProperty("role", user.getRole());
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("User logged in: " + phone);
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Login error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    private void incrementAttempts(HttpSession session, String phone, String attemptKey, String lockTimeKey) {
        Integer attempts = (Integer) session.getAttribute(attemptKey);
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        
        if (attempts >= MAX_ATTEMPTS) {
            session.setAttribute(lockTimeKey, System.currentTimeMillis());
            Logger.warn("Account locked due to failed attempts: " + phone);
        } else {
            session.setAttribute(attemptKey, attempts);
        }
    }
}
