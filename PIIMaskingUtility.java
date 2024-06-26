package com.example.springboot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PIIMaskingUtility {

	private static final Logger logger = LoggerFactory.getLogger(PIIMaskingUtility.class);

	private static final Marker piiMarker = MarkerFactory.getMarker("PII");

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	@Value("${pii.fields}")
	private String piiFields;

	public <T> void logTrace(T object) {
		log(Level.TRACE, object);
	}

	public <T> void logDebug(T object) {
		log(Level.DEBUG, object);
	}

	public <T> void logInfo(T object) {
		log(Level.INFO, object);
	}

	public <T> void logWarn(T object) {
		log(Level.WARN, object);
	}

	public <T> void logError(T object) {
		log(Level.ERROR, object);
	}

	private <T> void log(Level level, T object) {
		System.out.print("Inside Log() method.");
		executor.submit(() -> {
			String json = serializeToJson(object);
			String maskedJson = maskPIIData(json);
			switch (level) {
			case TRACE:
				logger.trace(piiMarker, maskedJson);
				break;
			case DEBUG:
				logger.debug(piiMarker, maskedJson);
				break;
			case INFO:
				logger.info(piiMarker, maskedJson);
				break;
			case WARN:
				logger.warn(piiMarker, maskedJson);
				break;
			case ERROR:
				logger.error(piiMarker, maskedJson);
				break;
			}
		});
	}

	private <T> String serializeToJson(T object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// Handle serialization exception
			logger.error("Error occurred during serialization to JSON: {}", e.getMessage());
			return "{}"; // Return an empty JSON object as fallback
		}
	}

	private String maskPIIData(String json) {
		if (!StringUtils.hasText(piiFields)) {
			return json; // No fields specified for masking
		}
		String[] fields = piiFields.split(",");
		for (String field : fields) {
			json = json.replaceAll("\"" + field.trim() + "\"\\s*:\\s*\"[^\"]+\"",
					"\"" + field.trim() + "\":\"Masked\"");
		}
		System.out.print(json);
		return json;
	}

	private enum Level {
		TRACE, DEBUG, INFO, WARN, ERROR
	}
}
