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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;

/**
 *
 * @author tuananh
 */
@WebServlet("/api/auth/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

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
                case "verify-reset":
                    verifyReset(req, res);
                    break;
                default:
                    JsonResponse.sendError(res, 400, "Action không hợp lệ");
                    break;
            }
            
        } catch (IOException e) {
            Logger.error("Forgot password error: " + e.getMessage());
            JsonResponse.sendError(res, 500, "Lỗi server");
        }
    }
    
    /**
     * UC-03  1-4: Request OTP
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
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByPhoneNumber(phone);
        
        if (user == null) {
            JsonResponse.sendError(res, 400, "Số điện thoại chưa được đăng ký");
            return;
        }
        
        // Generate OTP
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        
        HttpSession session = req.getSession();
        session.setAttribute("forgot_otp_" + phone, otp);
        session.setAttribute("forgot_otp_time_" + phone, System.currentTimeMillis());
        session.setMaxInactiveInterval(5 * 60);
        
        System.out.println("⚠️ DEBUG OTP cho " + phone + ": " + otp);
        
        JsonObject response = JsonResponse.success("OTP đã được gửi");
        JsonObject data = new JsonObject();
        data.addProperty("phoneNumber", phone);
        data.addProperty("debugOtp", otp);
        response.add("data", data);
        
        JsonResponse.sendJson(res, response);
    }
    
    /**
     * UC-03  5-10: Verify OTP & Reset Password
     */
    private void verifyReset(HttpServletRequest req, HttpServletResponse res) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder jsonData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonData.append(line);
        }
        
        JsonObject json = com.google.gson.JsonParser.parseString(jsonData.toString()).getAsJsonObject();
        
        String phone = json.get("phoneNumber").getAsString();
        String otp = json.get("otp").getAsString();
        String newPassword = json.get("newPassword").getAsString();
        String confirmPassword = json.get("confirmPassword").getAsString();
        
        // E4: Check OTP expired
        HttpSession session = req.getSession();
        Long otpTime = (Long) session.getAttribute("forgot_otp_time_" + phone);
        if (otpTime != null && (System.currentTimeMillis() - otpTime) > 5 * 60 * 1000) {
            JsonResponse.sendError(res, 400, "Mã OTP hết hạn");
            return;
        }
        
        // E2: Verify OTP
        String storedOtp = (String) session.getAttribute("forgot_otp_" + phone);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            JsonResponse.sendError(res, 400, "Mã OTP không chính xác");
            return;
        }
        
        // Validate password
        if (!newPassword.equals(confirmPassword)) {
            JsonResponse.sendError(res, 400, "Mật khẩu xác nhận không khớp");
            return;
        }
        
        if (!PasswordUtil.isValidPassword(newPassword)) {
            JsonResponse.sendError(res, 400, "Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByPhoneNumber(phone);
        
        if (user == null) {
            JsonResponse.sendError(res, 400, "Số điện thoại không tồn tại");
            return;
        }
        
        // Update password
        if (!userDAO.updatePassword(user.getId(), PasswordUtil.hashPassword(newPassword))) {
            JsonResponse.sendError(res, 500, "Cập nhật mật khẩu thất bại");
            return;
        }
        
        // Clear OTP
        session.removeAttribute("forgot_otp_" + phone);
        session.removeAttribute("forgot_otp_time_" + phone);
        
        JsonResponse.sendJson(res, JsonResponse.success("Đặt lại mật khẩu thành công"));
        Logger.info("Password reset for user: " + phone);
    }
}


