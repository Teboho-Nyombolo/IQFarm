package com.example.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent}")
    private String apiUrl;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(15));

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public String analyzePlantDisease(byte[] imageBytes, String mimeType, String prompt) {
        System.out.println("=== GEMINI CALL START ===");
        System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("API URL: " + apiUrl);

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("WARNING: GEMINI_API_KEY not set");
            throw new RuntimeException("GEMINI_API_KEY not configured");
        }

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> inlineData = Map.of(
                "mimeType", mimeType != null ? mimeType : "image/jpeg",
                "data", base64Image
        );

        Map<String, Object> imagePart = Map.of("inlineData", inlineData);
        Map<String, Object> textPart = Map.of("text", prompt);

        Map<String, Object> content = Map.of("parts", List.of(textPart, imagePart));

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 2048
                )
        );

        String url = apiUrl + "?key=" + apiKey;

        try {
            String responseBody = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String errorBody = new String(response.getBody().readAllBytes());
                        throw new RuntimeException("Gemini API error: HTTP " + response.getStatusCode() + " - " + errorBody);
                    })
                    .body(String.class);

            System.out.println("Response received successfully");
            return extractTextFromResponse(responseBody);

        } catch (RestClientException e) {
            System.err.println("Gemini API error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
        }
    }

    public String analyzeTextPrompt(String prompt) {
        System.out.println("=== GEMINI TEXT CALL START ===");
        System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("WARNING: GEMINI_API_KEY not set");
            throw new RuntimeException("GEMINI_API_KEY not configured");
        }

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of(
                        "temperature", 0.3,
                        "maxOutputTokens", 4096
                )
        );

        String url = apiUrl + "?key=" + apiKey;

        try {
            String responseBody = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String errorBody = new String(response.getBody().readAllBytes());
                        throw new RuntimeException("Gemini API error: HTTP " + response.getStatusCode() + " - " + errorBody);
                    })
                    .body(String.class);

            System.out.println("Text response received successfully");
            return extractTextFromResponse(responseBody);

        } catch (RestClientException e) {
            System.err.println("Gemini API error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
        }
    }

    private String extractTextFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode error = root.path("error");
            if (!error.isMissingNode()) {
                throw new RuntimeException("Gemini API error: " + error.path("message").asText());
            }

            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            throw new RuntimeException("Unexpected response format");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response", e);
        }
    }
}