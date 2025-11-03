package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.UserDAO;
import com.mini.ewallet.model.User;
import com.mini.ewallet.util.*;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/admin/users/*")
public class AdminUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            // Parse URL: /api/admin/users/2
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.sendError(res, 400, "User ID required");
                return;
            }
            
            Long userId = Long.valueOf(pathInfo.substring(1));
            
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findById(userId);
            
            if (user == null) {
                JsonResponse.sendError(res, 404, "User không tồn tại");
                return;
            }
            
            JsonObject response = JsonResponse.success("Lấy thông tin user thành công");
            JsonObject data = new JsonObject();
            data.addProperty("id", user.getId());
            data.addProperty("phoneNumber", user.getPhoneNumber());
            data.addProperty("fullName", user.getFullName());
            data.addProperty("role", user.getRole());
            data.addProperty("status", user.getStatus());
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            
        } catch (IOException | NumberFormatException e) {
            Logger.error("Admin user get error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            String pathInfo = req.getPathInfo();
            String action = req.getParameter("action");
            
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.sendError(res, 400, "User ID required");
                return;
            }
            
            Long userId = Long.valueOf(pathInfo.substring(1));
            
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findById(userId);
            
            if (user == null) {
                JsonResponse.sendError(res, 404, "User không tồn tại");
                return;
            }
            
            if (null == action) {
                JsonResponse.sendError(res, 400, "Action không hợp lệ");
            } else switch (action) {
                case "lock":
                    user.setStatus(Constants.STATUS_LOCKED);
                    if (userDAO.update(user)) {
                        JsonResponse.sendJson(res, JsonResponse.success("Khóa user thành công"));
                        Logger.info("User locked: " + userId);
                    } else {
                        JsonResponse.sendError(res, 500, "Khóa user thất bại");
                    }   break;
                case "unlock":
                    user.setStatus(Constants.STATUS_ACTIVE);
                    if (userDAO.update(user)) {
                        JsonResponse.sendJson(res, JsonResponse.success("Mở khóa user thành công"));
                        Logger.info("User unlocked: " + userId);
                    } else {
                        JsonResponse.sendError(res, 500, "Mở khóa user thất bại");
                    }   break;
                default:
                    JsonResponse.sendError(res, 400, "Action không hợp lệ");
                    break;
            }
            
        } catch (IOException | NumberFormatException e) {
            Logger.error("Admin user post error: " + e.getMessage());
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
