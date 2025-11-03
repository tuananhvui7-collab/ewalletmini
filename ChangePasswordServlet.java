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
import java.io.IOException;
import java.io.BufferedReader;

/**
 *
 * @author tuananh
 */
@WebServlet("/api/user/change-password")
public class ChangePasswordServlet extends HttpServlet {

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
            
            String oldPassword = json.get("oldPassword").getAsString();
            String newPassword = json.get("newPassword").getAsString();
            
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findById(userId);
            
            if (user == null) {
                JsonResponse.sendError(res, 404, "User không tồn tại");
                return;
            }
            
            if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
                JsonResponse.sendError(res, 400, "Mật khẩu cũ không chính xác");
                return;
            }
            
            if (!PasswordUtil.isValidPassword(newPassword)) {
                JsonResponse.sendError(res, 400, "Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            
            if (!userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword))) {
                JsonResponse.sendError(res, 500, "Cập nhật mật khẩu thất bại");
                return;
            }
            
            JsonResponse.sendJson(res, JsonResponse.success("Đổi mật khẩu thành công"));
            Logger.info("Password changed for user: " + userId);
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Change password error: " + e.getMessage());
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
