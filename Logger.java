package com.mini.ewallet.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    
    private static final String LOG_FILE = "logs/ewallet.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    static {
        new File("logs").mkdirs();
    }
    
    /**
     * Log info
     * @param message
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Log error
     * @param message
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * Log warning
     * @param message
     */
    public static void warn(String message) {
        log("WARN", message);
    }
    
    /**
     * Log debug
     * @param message
     */
    public static void debug(String message) {
        log("DEBUG", message);
    }
    
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] %s - %s%n", timestamp, level, message);
        
        // Console
        System.out.print(logMessage);
        
        // File
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logMessage);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}