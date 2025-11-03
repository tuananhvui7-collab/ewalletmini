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
import jakarta.servlet.http.HttpSession; // Thêm import
import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.Connection; // Thêm import
import java.sql.SQLException; // Thêm import

/**
 *
 * @author tuananh
 */
@WebServlet("/api/transaction/payment-qr")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        Connection conn = null; // [SỬA] Khai báo Connection cho transaction
        
        try {
            String token = extractToken(req);
            if (token == null || !JwtUtil.validateToken(token)) {
                JsonResponse.sendError(res, 401, "Unauthorized");
                return;
            }
            
            Long buyerId = JwtUtil.getUserIdFromToken(token);
            
            BufferedReader reader = req.getReader();
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            
            JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
            
            String qrCode = json.get("qrCode").getAsString();
            BigDecimal amount = new BigDecimal(json.get("amount").getAsString());
            
            // [SỬA] Lấy "vé" giao dịch
            String ticket = json.get("transactionTicket").getAsString();

            // [SỬA] Kiểm tra "vé" giao dịch
            HttpSession session = req.getSession();
            String storedTicket = (String) session.getAttribute("TXN_TICKET_" + buyerId);
            Long expiry = (Long) session.getAttribute("TXN_TICKET_EXPIRY_" + buyerId);

            session.removeAttribute("TXN_TICKET_" + buyerId);
            session.removeAttribute("TXN_TICKET_EXPIRY_" + buyerId);

            if (storedTicket == null || expiry == null || !storedTicket.equals(ticket) || System.currentTimeMillis() > expiry) {
                JsonResponse.sendError(res, 403, "Phiên giao dịch không hợp lệ hoặc đã hết hạn.");
                return;
            }

            // Get buyer
            UserDAO userDAO = new UserDAO();
            User buyer = userDAO.findById(buyerId);
            if (buyer == null) {
                JsonResponse.sendError(res, 404, "Buyer không tồn tại");
                return;
            }
            
            // LOGIC CHECK PIN ĐÃ BỊ XÓA
            
            // Find merchant by QR code
            MerchantDAO merchantDAO = new MerchantDAO();
            Merchant qr = merchantDAO.findByQRCode(qrCode);
            if (qr == null) {
                JsonResponse.sendError(res, 400, "Mã QR không hợp lệ");
                return;
            }
            
            User merchant = userDAO.findById(qr.getMerchantId());
            if (merchant == null) {
                JsonResponse.sendError(res, 404, "Merchant không tồn tại");
                return;
            }
            if (!merchant.getStatus().equals(Constants.STATUS_ACTIVE)) {
                JsonResponse.sendError(res, 400, "Merchant không hoạt động");
                return;
            }
            
            // [SỬA] Bắt đầu Database Transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            WalletDAO walletDAO = new WalletDAO();

            // 1. Trừ tiền người mua
            if (!walletDAO.debit(conn, buyerId, amount)) {
                conn.rollback();
                JsonResponse.sendError(res, 400, "Số dư không đủ");
                return;
            }
            
            // 2. Cộng tiền merchant
            if (!walletDAO.credit(conn, merchant.getId(), amount)) {
                conn.rollback();
                JsonResponse.sendError(res, 500, "Cập nhật ví merchant thất bại");
                return;
            }
            
            // 3. Ghi log
            Transaction txn = new Transaction(
                Constants.TXN_PAYMENT,
                buyerId,
                merchant.getId(),
                amount,
                Constants.TXN_STATUS_COMPLETED,
                "Thanh toán QR"
            );
            txn.setReferenceCode("TXN_" + System.currentTimeMillis());
            
            TransactionDAO txnDAO = new TransactionDAO();
            txnDAO.create(txn); // Cần sửa create để nhận conn
            
            // [SỬA] Commit
            conn.commit();
            
            Wallet buyerWallet = walletDAO.getByUserId(buyerId);
            
            JsonObject response = JsonResponse.success("Thanh toán thành công");
            JsonObject data = new JsonObject();
            data.addProperty("amount", amount);
            data.addProperty("newBalance", buyerWallet.getBalance());
            data.addProperty("referenceCode", txn.getReferenceCode());
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Payment success: " + buyerId + " -> " + merchant.getId());
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Payment error: " + e.getMessage());
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
            Logger.error("Payment SQL error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi CSDL khi thanh toán");
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
    
    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    // HÀM verifyPin ĐÃ BỊ XÓA
}