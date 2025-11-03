package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.UserDAO;
import com.mini.ewallet.dao.WalletDAO;
import com.mini.ewallet.model.User;
import com.mini.ewallet.model.Wallet;
import com.mini.ewallet.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;

@WebServlet("/api/user/profile")
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findById(userId);
            
            if (user == null) {
                JsonResponse.sendError(res, 404, "User không tồn tại");
                return;
            }
            
            WalletDAO walletDAO = new WalletDAO();
            Wallet wallet = walletDAO.getByUserId(userId);
            
            JsonObject response = JsonResponse.success("Lấy hồ sơ thành công");
            JsonObject data = new JsonObject();
            data.addProperty("id", user.getId());
            data.addProperty("phoneNumber", user.getPhoneNumber());
            data.addProperty("email", user.getEmail());
            data.addProperty("fullName", user.getFullName());
            data.addProperty("role", user.getRole());
            data.addProperty("status", user.getStatus());
            data.addProperty("balance", wallet != null ? wallet.getBalance() : BigDecimal.ZERO);
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            
        } catch (IOException e) {
            Logger.error("Get profile error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
            
            String fullName = json.get("fullName").getAsString();
            
            if (!ValidationUtil.isValidName(fullName)) {
                JsonResponse.sendError(res, 400, "Họ tên không hợp lệ");
                return;
            }
            
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findById(userId);
            
            if (user == null) {
                JsonResponse.sendError(res, 404, "User không tồn tại");
                return;
            }
            
            user.setFullName(fullName);
            
            if (userDAO.update(user)) {
                JsonResponse.sendJson(res, JsonResponse.success("Cập nhật hồ sơ thành công"));
                Logger.info("Profile updated for user: " + userId);
            } else {
                JsonResponse.sendError(res, 500, "Cập nhật hồ sơ thất bại");
            }
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Update profile error: " + e.getMessage());
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
