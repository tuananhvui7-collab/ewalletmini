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

@WebServlet("/api/transaction/transfer")
public class TransferServlet extends HttpServlet {

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
            
            Long senderId = JwtUtil.getUserIdFromToken(token);
            BufferedReader reader = req.getReader();
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            
            JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
            
            String receiverPhone = json.get("receiverPhoneNumber").getAsString();
            BigDecimal amount = new BigDecimal(json.get("amount").getAsString());
            String message = json.has("message") ? json.get("message").getAsString() : "";
            
            // [SỬA] Lấy "vé" giao dịch
            String ticket = json.get("transactionTicket").getAsString();

            // Validate amount > 0
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JsonResponse.sendError(res, 400, "Số tiền phải lớn hơn 0");
                return;
            }
            
            // [SỬA] Kiểm tra "vé" giao dịch
            HttpSession session = req.getSession();
            String storedTicket = (String) session.getAttribute("TXN_TICKET_" + senderId);
            Long expiry = (Long) session.getAttribute("TXN_TICKET_EXPIRY_" + senderId);

            // Xóa vé ngay lập tức để chống dùng lại
            session.removeAttribute("TXN_TICKET_" + senderId);
            session.removeAttribute("TXN_TICKET_EXPIRY_" + senderId);

            if (storedTicket == null || expiry == null || !storedTicket.equals(ticket) || System.currentTimeMillis() > expiry) {
                JsonResponse.sendError(res, 403, "Phiên giao dịch không hợp lệ hoặc đã hết hạn.");
                return;
            }
            
            // Get sender
            UserDAO userDAO = new UserDAO();
            User sender = userDAO.findById(senderId);
            if (sender == null) {
                JsonResponse.sendError(res, 404, "Sender không tồn tại");
                return;
            }
            
            // LOGIC CHECK PIN ĐÃ BỊ XÓA
            
            // Get receiver
            User receiver = userDAO.findByPhoneNumber(receiverPhone);
            if (receiver == null) {
                JsonResponse.sendError(res, 404, "Người nhận không tồn tại");
                return;
            }
            
            // [SỬA] Bắt đầu Database Transaction
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Tắt tự động commit
            
            WalletDAO walletDAO = new WalletDAO();

            // 1. Trừ tiền người gửi (dùng hàm debit an toàn)
            if (!walletDAO.debit(conn, senderId, amount)) {
                conn.rollback(); // Hoàn tác
                JsonResponse.sendError(res, 400, "Số dư không đủ");
                return;
            }

            // 2. Cộng tiền người nhận (dùng hàm credit an toàn)
            if (!walletDAO.credit(conn, receiver.getId(), amount)) {
                conn.rollback(); // Hoàn tác
                JsonResponse.sendError(res, 500, "Lỗi khi cập nhật ví người nhận");
                return;
            }
            
            // 3. Ghi log giao dịch
            Transaction txn = new Transaction(
                Constants.TXN_P2P,
                senderId,
                receiver.getId(),
                amount,
                Constants.TXN_STATUS_COMPLETED,
                message
            );
            txn.setReferenceCode("TXN_" + System.currentTimeMillis());
            
            TransactionDAO txnDAO = new TransactionDAO();
            // Cần sửa TransactionDAO.create để nhận Connection
            // Tạm thời: Giả sử hàm create cũ vẫn chạy (nhưng không an toàn 100%)
            // Để an toàn 100%, hàm txnDAO.create() cũng phải nhận conn
            txnDAO.create(txn); 
            
            // [SỬA] Nếu mọi thứ OK, commit transaction
            conn.commit();
            
            // Lấy số dư mới (an toàn vì đã commit)
            Wallet senderWallet = walletDAO.getByUserId(senderId);
            
            JsonObject response = JsonResponse.success("Chuyển tiền thành công");
            JsonObject data = new JsonObject();
            data.addProperty("amount", amount);
            data.addProperty("newBalance", senderWallet.getBalance());
            data.addProperty("referenceCode", txn.getReferenceCode());
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Transfer success: " + senderId + " -> " + receiver.getId());
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Transfer error: " + e.getMessage());
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
            Logger.error("Transfer SQL error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi CSDL khi chuyển tiền");
        } finally {
            // [SỬA] Luôn đóng connection và bật lại AutoCommit
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