package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.AdminDAO;
import com.mini.ewallet.model.Admin;
import com.mini.ewallet.util.JsonResponse;
import com.mini.ewallet.util.JwtUtil;
import com.mini.ewallet.util.Logger;
import com.mini.ewallet.util.PasswordUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Servlet này CHỈ DÀNH CHO ADMIN đăng nhập
 * Nó sẽ gọi AdminDAO (thay vì UserDAO)
 */
@WebServlet("/api/admin/login") // <--- Địa chỉ (path) mới
public class AdminLoginServlet extends HttpServlet {
    
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
            
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();

            // Gọi AdminDAO
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.findByUsername(username);

            if (admin == null) {
                JsonResponse.sendError(res, 400, "Tên đăng nhập hoặc mật khẩu không đúng");
                return;
            }
            
            // Check status (ACTIVE, LOCKED)
            if (!admin.getStatus().equals("ACTIVE")) {
                JsonResponse.sendError(res, 403, "Tài khoản của bạn đã bị khóa");
                return;
            }

            // Verify password
            if (!PasswordUtil.verifyPassword(password, admin.getPassword())) {
                JsonResponse.sendError(res, 400, "Tên đăng nhập hoặc mật khẩu không đúng");
                return;
            }
            
            // Đăng nhập thành công, tạo Token
            // Chúng ta dùng Admin ID và Admin Username để tạo token
            String token = JwtUtil.generateToken(admin.getId(), admin.getUsername());
            
            JsonObject response = JsonResponse.success("Đăng nhập Admin thành công");
            JsonObject data = new JsonObject();
            data.addProperty("userId", admin.getId());
            data.addProperty("username", admin.getUsername());
            data.addProperty("fullName", admin.getFullName());
            data.addProperty("token", token);
            data.addProperty("role", admin.getRole()); // Gửi role về
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Admin logged in: " + username);
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Admin Login error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
}