package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.TransactionDAO;
import com.mini.ewallet.model.Transaction;
import com.mini.ewallet.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/admin/reports")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            String type = req.getParameter("type");
            if (type == null) type = "dashboard";
            
            TransactionDAO txnDAO = new TransactionDAO();
            
            if ("dashboard".equals(type)) {
                // Simple dashboard summary
                JsonObject response = JsonResponse.success("Lấy báo cáo thành công");
                JsonObject data = new JsonObject();
                data.addProperty("totalTransactions", 0);
                data.addProperty("totalAmount", 0);
                data.addProperty("totalUsers", 0);
                response.add("data", data);
                JsonResponse.sendJson(res, response);
            } else {
                JsonResponse.sendError(res, 400, "Type không hợp lệ");
            }
            
        } catch (IOException e) {
            Logger.error("Report error: " + e.getMessage());
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
