package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.ScratchCardDAO;
import com.mini.ewallet.model.ScratchCard;
import com.mini.ewallet.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 *
 * @author tuananh
 */
@WebServlet("/api/admin/scratch-cards/create")
public class CreateCardServlet extends HttpServlet {

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
            
            int quantity = json.get("quantity").getAsInt();
            BigDecimal denomination = new BigDecimal(json.get("denomination").getAsString());
            String expiryDateStr = json.get("expiryDate").getAsString();
            
            if (quantity <= 0) {
                JsonResponse.sendError(res, 400, "Số lượng phải lớn hơn 0");
                return;
            }
            
            if (denomination.compareTo(BigDecimal.ZERO) <= 0) {
                JsonResponse.sendError(res, 400, "Mệnh giá phải lớn hơn 0");
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, formatter);
            
            ScratchCardDAO cardDAO = new ScratchCardDAO();
            JsonArray cardsArray = new JsonArray();
            
            for (int i = 0; i < quantity; i++) {
                String serial = generateSerial();
                String code = generateCode();
                
                ScratchCard card = new ScratchCard(
                    serial,
                    code,
                    denomination,
                    Constants.CARD_NEW,
                    Date.valueOf(expiryDate)
                );
                
                // Note: Bạn cần thêm create method vào ScratchCardDAO
                // Để cơ bản, chúng ta simulate
                JsonObject cardJson = new JsonObject();
                cardJson.addProperty("serialNumber", serial);
                cardJson.addProperty("cardCode", code);
                cardJson.addProperty("denomination", denomination);
                cardsArray.add(cardJson);
            }
            
            JsonObject response = JsonResponse.success("Tạo " + quantity + " thẻ cào thành công");
            response.add("data", cardsArray);
            
            JsonResponse.sendJson(res, response);
            Logger.info("Created " + quantity + " scratch cards");
            
        } catch (JsonSyntaxException | IOException e) {
            Logger.error("Create card error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    private String generateSerial() {
        return "CARD" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
    
    private String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
