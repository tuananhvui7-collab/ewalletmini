package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.*;
import com.mini.ewallet.model.*;
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
import java.math.BigDecimal;
import java.sql.Connection; // Thêm import
import java.sql.Date;
import java.sql.SQLException; // Thêm import

/**
 *
 * @author tuananh
 */
@WebServlet("/api/transaction/topup")
public class TopupServlet extends HttpServlet {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        Connection conn = null; // [SỬA] Khai báo Connection
        
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            HttpSession session = req.getSession();
            String lockKey = "topup_lock_" + userId;
            String attemptKey = "topup_attempts_" + userId;
            
            // Check if locked
            Long lockTime = (Long) session.getAttribute(lockKey);
            if (lockTime != null && (System.currentTimeMillis() - lockTime) < LOCK_DURATION) {
                JsonResponse.sendError(res, 400, "Chức năng nạp tiền tạm khóa 15 phút");
                return;
            }
            
            BufferedReader reader = req.getReader();
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            
            JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
            
            String serial = json.get("serialNumber").getAsString();
            String code = json.get("cardCode").getAsString();
            
            // Find scratch card
            ScratchCardDAO cardDAO = new ScratchCardDAO();
            ScratchCard card = cardDAO.findBySerialAndCode(serial, code);
            
            // E1: Card invalid/used/expired
            if (card == null) {
                incrementAttempts(session, userId, attemptKey, lockKey);
                JsonResponse.sendError(res, 400, "Mã thẻ không tồn tại, đã sử dụng hoặc hết hạn");
                return;
            }
            
            // E1: Check status is NEW
            if (!card.getStatus().equals(Constants.CARD_NEW)) {
                incrementAttempts(session, userId, attemptKey, lockKey);
                JsonResponse.sendError(res, 400, "Thẻ này đã được sử dụng hoặc hết hạn");
                return;
            }
            
            // E1: Check expiry date
            Date today = new Date(System.currentTimeMillis());
            if (card.getExpiryDate().before(today)) {
                // Thẻ hết hạn, cập nhật trạng thái
                cardDAO.updateCardStatus(serial, Constants.CARD_EXPIRED, null);
                incrementAttempts(session, userId, attemptKey, lockKey);
                JsonResponse.sendError(res, 400, "Thẻ này đã hết hạn sử dụng");
                return;
            }
            
            // [SỬA] Bắt đầu Database Transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            WalletDAO walletDAO = new WalletDAO();
            
            // 1. Cộng tiền vào ví
            if (!walletDAO.credit(conn, userId, card.getDenomination())) {
                conn.rollback();
                JsonResponse.sendError(res, 500, "Xử lý lỗi, vui lòng thử lại sau (err: W-01)");
                return;
            }
                
            // 2. Cập nhật trạng thái thẻ
            // Cần sửa updateCardStatus để nhận Connection
            if (!cardDAO.updateCardStatus(serial, Constants.CARD_USED, userId)) {
                conn.rollback();
                JsonResponse.sendError(res, 500, "Xử lý lỗi, vui lòng thử lại sau (err: C-01)");
                return;
            }
                
            // 3. Ghi log
            Transaction txn = new Transaction(
                Constants.TXN_TOPUP,
                null,
                userId,
                card.getDenomination(),
                Constants.TXN_STATUS_COMPLETED,
                "Nạp tiền bằng thẻ cào"
            );
            txn.setReferenceCode("TXN_" + System.currentTimeMillis());
                
            TransactionDAO txnDAO = new TransactionDAO();
            txnDAO.create(txn); // Cần sửa create để nhận conn
                
            // [SỬA] Commit
            conn.commit();

            // Reset attempts
            session.removeAttribute(attemptKey);
            session.removeAttribute(lockKey);
                
            Wallet wallet = walletDAO.getByUserId(userId);

            // Response
            JsonObject response = JsonResponse.success("Nạp tiền thành công");
            JsonObject data = new JsonObject();
            data.addProperty("amount", card.getDenomination());
            data.addProperty("newBalance", wallet.getBalance());
            data.addProperty("referenceCode", txn.getReferenceCode());
            response.add("data", data);
                
            JsonResponse.sendJson(res, response);
            Logger.info("Topup success for user: " + userId);
                
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Topup error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        } catch (SQLException e) {
            // [SỬA] Xử lý lỗi SQL và rollback
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    Logger.error("Rollback failed: " + ex.getMessage());
                }
            }
            Logger.error("Topup SQL error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi CSDL khi nạp tiền");
        } finally {
            // [SỬA] Luôn đóng connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    Logger.error("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }
    
    private void incrementAttempts(HttpSession session, Long userId, String attemptKey, String lockKey) {
        Integer attempts = (Integer) session.getAttribute(attemptKey);
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        
        if (attempts >= MAX_ATTEMPTS) {
            session.setAttribute(lockKey, System.currentTimeMillis());
            Logger.warn("Topup locked for user: " + userId);
        } else {
            session.setAttribute(attemptKey, attempts);
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