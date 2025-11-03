package com.mini.ewallet.servlet;

import com.mini.ewallet.dao.UserDAO;
import com.mini.ewallet.dao.WalletDAO;
import com.mini.ewallet.model.User;
import com.mini.ewallet.util.*;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;

/**
 *
 * @author tuananh
 */
@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            
            if (null == action) {
                JsonResponse.sendError(res, 400, "Action không hợp lệ");
            } else switch (action) {
                case "request-otp":
                    requestOTP(req, res);
                    break;
                case "verify-register":
                    verifyRegister(req, res);
                    break;
                default:
                    JsonResponse.sendError(res, 400, "Action không hợp lệ");
                    break;
            }
            
        } catch (IOException e) {
            Logger.error("Register error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    /**
     * UC-01  1-3: Request OTP
     */
    private void requestOTP(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder jsonData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonData.append(line);
        }
        
        JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
        
        String phone = json.get("phoneNumber").getAsString();
        String contactType = json.get("contactType").getAsString(); // "phone" hoặc "email"
        
        // Validate
        if (!ValidationUtil.isValidPhoneNumber(phone)) {
            JsonResponse.sendError(res, 400, "Số điện thoại không hợp lệ");
            return;
        }
        
        // Check phone already exists
        UserDAO userDAO = new UserDAO();
        if (userDAO.findByPhoneNumber(phone) != null) {
            JsonResponse.sendError(res, 400, "Số điện thoại này đã được đăng ký");
            return;
        }
        
        // Generate OTP (6 digits)
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        
        // Store OTP in session (thực tế dùng Redis hoặc DB)
        HttpSession session = req.getSession();
        session.setAttribute("otp_" + phone, otp);
        session.setAttribute("otp_time_" + phone, System.currentTimeMillis());
        session.setMaxInactiveInterval(5 * 60); // 5 phút
        
        // Log OTP (để test)
        System.out.println("⚠️ DEBUG OTP cho " + phone + ": " + otp);
        Logger.info("OTP sent to: " + phone);
        
        JsonObject response = JsonResponse.success("OTP đã được gửi");
        JsonObject data = new JsonObject();
        data.addProperty("phoneNumber", phone);
        data.addProperty("debugOtp", otp); // Remove in production
        response.add("data", data);
        
        JsonResponse.sendJson(res, response);
    }
    
    /**
     * UC-01:   4-10: Verify OTP & Register
     */
    private void verifyRegister(HttpServletRequest req, HttpServletResponse res) throws IOException {
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
        String password = json.get("password").getAsString();
        String pin = json.get("transactionPin").getAsString();
        String otp = json.get("otp").getAsString();
        
        // Validate input
        if (!ValidationUtil.isValidPhoneNumber(phone)) {
            JsonResponse.sendError(res, 400, "Số điện thoại không hợp lệ");
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            JsonResponse.sendError(res, 400, "Email không hợp lệ");
            return;
        }
        
        if (!ValidationUtil.isValidName(fullName)) {
            JsonResponse.sendError(res, 400, "Họ tên không hợp lệ");
            return;
        }
        
        if (!PasswordUtil.isValidPassword(password)) {
            JsonResponse.sendError(res, 400, "Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        
        if (!PasswordUtil.isValidPin(pin)) {
            JsonResponse.sendError(res, 400, "Mã PIN phải là 6 chữ số");
            return;
        }
        
        // E5: Check OTP expired
        HttpSession session = req.getSession();
        Long otpTime = (Long) session.getAttribute("otp_time_" + phone);
        if (otpTime != null && (System.currentTimeMillis() - otpTime) > 5 * 60 * 1000) {
            JsonResponse.sendError(res, 400, "Mã OTP hết hạn");
            return;
        }
        
        // E2: Verify OTP
        String storedOtp = (String) session.getAttribute("otp_" + phone);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            JsonResponse.sendError(res, 400, "Mã OTP không chính xác");
            return;
        }
        
        // Check phone/email already exists
        UserDAO userDAO = new UserDAO();
        if (userDAO.findByPhoneNumber(phone) != null) {
            JsonResponse.sendError(res, 400, "Số điện thoại đã được đăng ký");
            return;
        }
        
        if (userDAO.findByEmail(email) != null) {
            JsonResponse.sendError(res, 400, "Email đã được đăng ký");
            return;
        }
        
        // Create user
        User user = new User(
            phone,
            email,
            fullName,
            PasswordUtil.hashPassword(password),
            PasswordUtil.hashPassword(pin),
            Constants.ROLE_USER,
            Constants.STATUS_ACTIVE
        );
        
        // NFR1: Mã hóa mật khẩu + PIN
        if (!userDAO.register(user)) {
            JsonResponse.sendError(res, 500, "Đăng ký thất bại");
            return;
        }
        
        // Get registered user
        User registeredUser = userDAO.findByPhoneNumber(phone);
        
        // Create wallet
        WalletDAO walletDAO = new WalletDAO();
        walletDAO.create(registeredUser.getId());
        
        // Generate token
        String token = JwtUtil.generateToken(registeredUser.getId(), phone);
        
        // Clear OTP from session
        session.removeAttribute("otp_" + phone);
        session.removeAttribute("otp_time_" + phone);
        
        JsonObject response = JsonResponse.success("Đăng ký thành công");
        JsonObject data = new JsonObject();
        data.addProperty("userId", registeredUser.getId());
        data.addProperty("phoneNumber", phone);
        data.addProperty("fullName", fullName);
        data.addProperty("token", token);
        data.addProperty("role", Constants.ROLE_USER);
        response.add("data", data);
        
        JsonResponse.sendJson(res, response);
        Logger.info("User registered: " + phone);
    }
}
