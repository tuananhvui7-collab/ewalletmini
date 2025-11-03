package com.mini.ewallet.servlet;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Test API - không cần token
 */
@WebServlet("/api/test")
public class TestServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("message", "API hoạt động!");
            
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().println(response.toString());
            
        } catch (Exception e) {
            res.setStatus(500);
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("message", e.getMessage());
            res.getWriter().println(error.toString());
        }
    }
}