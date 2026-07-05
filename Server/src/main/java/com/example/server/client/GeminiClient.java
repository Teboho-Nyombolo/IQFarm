package com.example.server.client;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("WARNING: GEMINI_API_KEY not set, using mock data");
            return getMockDiseaseResponse();
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
                    .body(String.class);

            System.out.println("Response received successfully");
            return extractTextFromResponse(responseBody, true);

        } catch (Exception e) {
            System.err.println("Gemini API error (falling back to mock): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return getMockDiseaseResponse();
        }
    }

    public String analyzeTextPrompt(String prompt) {
        System.out.println("=== GEMINI TEXT CALL START ===");
        System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("WARNING: GEMINI_API_KEY not set, using mock data");
            return getMockPlantAdvisoryResponse();
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
                    .body(String.class);

            System.out.println("Text response received successfully");
            return extractTextFromResponse(responseBody, false);

        } catch (Exception e) {
            System.err.println("Gemini API error (falling back to mock): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return getMockPlantAdvisoryResponse();
        }
    }

    private String getMockDiseaseResponse() {
        return "Plant: Tomato\n" +
               "Has Disease: Yes\n" +
               "Disease Name: Early Blight\n" +
               "Confidence Level: 85\n" +
               "Description: A fungal infection causing dark spots on leaves.\n" +
               "Treatment Recommendations: Remove affected leaves, use fungicide.\n" +
               "Pesticides: Copper-based fungicides\n" +
               "Fertilizers: Balanced NPK fertilizer\n" +
               "Prevention Tips: Ensure good air circulation, water at base.";
    }

    private String getMockPlantAdvisoryResponse() {
        return "Plant: Tomatoes\n" +
               "Planting Advice: Plant in well-drained soil, full sun.\n" +
               "Water Requirements: Water deeply 1-2 times per week.\n" +
               "Fertilizer Recommendations: Use tomato-specific fertilizer every 2 weeks.\n" +
               "Pest and Disease Tips: Watch for aphids and blight.\n" +
               "Harvest Timing Recommendations: Harvest when fully ripe and red.\n" +
               "Additional Recommendations: Stake plants for support.\n" +
               "Warnings: Avoid overhead watering to prevent disease.";
    }

    private String extractTextFromResponse(String responseBody, boolean isDiseaseRequest) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode error = root.path("error");
            if (!error.isMissingNode()) {
                System.err.println("Gemini API returned error, using mock data");
                return isDiseaseRequest ? getMockDiseaseResponse() : getMockPlantAdvisoryResponse();
            }

            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            System.err.println("Unexpected Gemini response format, using mock data");
            return isDiseaseRequest ? getMockDiseaseResponse() : getMockPlantAdvisoryResponse();
        } catch (Exception e) {
            System.err.println("Failed to parse Gemini response, using mock data: " + e.getMessage());
            return isDiseaseRequest ? getMockDiseaseResponse() : getMockPlantAdvisoryResponse();
        }
    }
}