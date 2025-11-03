package com.mini.ewallet.util;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponse {
    
    /**
     * Send JSON response
     * @param response
     * @param json
     * @throws java.io.IOException
     */
    public static void sendJson(HttpServletResponse response, JsonObject json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
    
    /**
     * Success response
     * @param message
     * @return 
     */
    public static JsonObject success(String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        obj.addProperty("message", message);
        return obj;
    }
    
    /**
     * Success with data
     * @param message
     * @param data
     * @return 
     */
    public static JsonObject success(String message, Object data) {
        JsonObject obj = success(message);
        obj.add("data", com.google.gson.JsonParser.parseString(
                new com.google.gson.Gson().toJson(data)
        ));
        return obj;
    }
    
    /**
     * Error response
     * @param message
     * @return 
     */
    public static JsonObject error(String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("success", false);
        obj.addProperty("message", message);
        return obj;
    }
    
    /**
     * Send error response
     * @param response
     * @param statusCode
     * @param message
     * @throws java.io.IOException
     */
    public static void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(error(message).toString());
    }
}
