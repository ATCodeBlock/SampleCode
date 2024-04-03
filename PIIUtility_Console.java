package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PIIMasker {
    private static final Logger logger = LoggerFactory.getLogger(PIIMasker.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void logInfo(Object object, String... maskedFields) {
        log(object, "INFO", maskedFields);
    }

    public static void logError(Object object, String... maskedFields) {
        log(object, "ERROR", maskedFields);
    }

    public static void logDebug(Object object, String... maskedFields) {
        log(object, "DEBUG", maskedFields);
    }

    private static void log(Object object, String level, String... maskedFields) {
        executor.submit(() -> {
            try {
                String jsonLog = toJsonString(object);
                String maskedLog = maskPIIData(jsonLog, maskedFields);
                if ("INFO".equalsIgnoreCase(level))
                    logger.info(maskedLog);
                else if ("ERROR".equalsIgnoreCase(level))
                    logger.error(maskedLog);
                else if ("DEBUG".equalsIgnoreCase(level))
                    logger.debug(maskedLog);
            } catch (JsonProcessingException e) {
                logger.error("Error processing JSON", e);
            }
        });
    }

    private static String toJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private static String maskPIIData(String jsonLog, String[] maskedFields) {
        // Code to mask PII data in JSON string
        // For example, you can replace specific fields with "MASKED"
        // Ensure the logic is implemented according to your specific requirements
        return jsonLog; // Placeholder for masking logic
    }
}
