// ============= UC-09: GET HISTORY SERVLET =============
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

/**
 *
 * @author tuananh
 */
@WebServlet("/api/transaction/history")
public class GetBalance_HistoryServlet extends HttpServlet {

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
            int limit = 20;
            
            String limitParam = req.getParameter("limit");
            if (limitParam != null) {
                try {
                    limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException ignored) {}
            }
            
            TransactionDAO txnDAO = new TransactionDAO();
            List<Transaction> transactions = txnDAO.getUserHistory(userId, limit);
            
            JsonObject response = JsonResponse.success("Lấy lịch sử thành công");
            JsonArray dataArray = new JsonArray();
            
            for (Transaction txn : transactions) {
                JsonObject txnJson = new JsonObject();
                txnJson.addProperty("id", txn.getId());
                txnJson.addProperty("type", txn.getType());
                txnJson.addProperty("amount", txn.getAmount());
                txnJson.addProperty("status", txn.getStatus());
                txnJson.addProperty("description", txn.getDescription());
                txnJson.addProperty("createdAt", txn.getCreatedAt().toString());
                dataArray.add(txnJson);
            }
            
            response.add("data", dataArray);
            JsonResponse.sendJson(res, response);
            
        } catch (IOException e) {
            Logger.error("Get history error: " + e.getMessage());
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