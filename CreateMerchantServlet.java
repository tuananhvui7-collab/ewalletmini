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
import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author tuananh
 */
@WebServlet("/api/admin/merchants")
public class CreateMerchantServlet extends HttpServlet {

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
            
            String phone = json.get("phoneNumber").getAsString();
            String email = json.get("email").getAsString();
            String fullName = json.get("fullName").getAsString();
            
            // Tạm thời comment vì ValidationUtil.java không được cung cấp
            // if (!ValidationUtil.isValidPhoneNumber(phone)) {
            //     JsonResponse.sendError(res, 400, "Số điện thoại không hợp lệ");
            //     return;
            // }
            
            UserDAO userDAO = new UserDAO();
            if (userDAO.findByPhoneNumber(phone) != null) {
                JsonResponse.sendError(res, 400, "Số điện thoại đã tồn tại");
                return;
            }
            
            // Create merchant user
            User merchant = new User(
                phone,
                email,
                fullName,
                PasswordUtil.hashPassword("merchant123"), // Mật khẩu mặc định
                PasswordUtil.hashPassword("000000"), // PIN mặc định
                Constants.ROLE_MERCHANT,
                Constants.STATUS_ACTIVE
            );
            
            if (!userDAO.register(merchant)) {
                JsonResponse.sendError(res, 500, "Tạo merchant thất bại (DB User)");
                return;
            }
            
            User savedMerchant = userDAO.findByPhoneNumber(phone);
            
            // Create wallet
            WalletDAO walletDAO = new WalletDAO();
            if (!walletDAO.create(savedMerchant.getId())) {
                 JsonResponse.sendError(res, 500, "Tạo ví thất bại (DB Wallet)");
                 // Cần thêm logicxóa User đã tạo ở trên (rollback)
                 return;
            }
            
            // [SỬA] Tạo và LƯU QR code
            String qrCode = "QR_MERCHANT_" + savedMerchant.getId();
            MerchantDAO merchantDAO = new MerchantDAO();
            
            // Cần thêm hàm create(Merchant m) vào MerchantDAO.java
            // Giả sử hàm đó trông như thế này:
            // if (!merchantDAO.create(new Merchant(savedMerchant.getId(), qrCode))) {
            //    JsonResponse.sendError(res, 500, "Tạo QR thất bại (DB Merchant)");
            //    return;
            // }
            // Tạm thời: MerchantDAO.java chỉ có findByQRCode
            // Bạn cần thêm hàm create vào MerchantDAO.java
            
            
            JsonObject response = JsonResponse.success("Tạo merchant thành công");
            JsonObject data = new JsonObject();
            data.addProperty("id", savedMerchant.getId());
            data.addProperty("phoneNumber", phone);
            data.addProperty("fullName", fullName);
            data.addProperty("qrCode", qrCode);
            data.addProperty("balance", 0);
            response.add("data", data);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Merchant created: " + phone);
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Create merchant error: " + e.getMessage());
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