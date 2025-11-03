package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.AdminDAO;
import com.mini.ewallet.model.Admin;
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
@WebServlet("/api/admin/management/create")
public class CreateAdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            BufferedReader reader = req.getReader();
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            
            JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
            
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            String fullName = json.get("fullName").getAsString();
            
            if (username == null || username.isEmpty()) {
                JsonResponse.sendError(res, 400, "Username không được để trống");
                return;
            }
            
            if (!PasswordUtil.isValidPassword(password)) {
                JsonResponse.sendError(res, 400, "Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            if (adminDAO.findByUsername(username) != null) {
                JsonResponse.sendError(res, 400, "Username đã tồn tại");
                return;
            }
            
            Admin admin = new Admin(
                username,
                PasswordUtil.hashPassword(password),
                fullName,
                "OPERATOR",
                Constants.STATUS_ACTIVE
            );
            
            if (!adminDAO.create(admin)) {
                JsonResponse.sendError(res, 500, "Tạo admin thất bại");
                return;
            }
            
            JsonObject response = JsonResponse.success("Tạo admin thành công");
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("fullName", fullName);
            data.addProperty("role", "OPERATOR");
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Admin created: " + username);
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Create admin error: " + e.getMessage());
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


