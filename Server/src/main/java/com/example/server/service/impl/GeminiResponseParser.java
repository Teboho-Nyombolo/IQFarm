package com.example.server.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GeminiResponseParser {

    public Map<String, String> parse(String rawResponse) {
        Map<String, String> result = new HashMap<>();
        result.put("hasDisease", "NO");
        result.put("diseaseName", "None");
        result.put("confidenceLevel", "0");
        result.put("description", "");
        result.put("treatmentRecommendations", "");
        result.put("pesticides", "");
        result.put("preventionTips", "");

        String[] lines = rawResponse.split("\n");

        StringBuilder currentField = new StringBuilder();
        String currentKey = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("HAS_DISEASE:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "hasDisease";
                currentField = new StringBuilder(extractValue(line));
            } else if (line.startsWith("DISEASE_NAME:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "diseaseName";
                currentField = new StringBuilder(extractValue(line));
            } else if (line.startsWith("CONFIDENCE_LEVEL:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "confidenceLevel";
                currentField = new StringBuilder(extractValue(line));
            } else if (line.startsWith("DESCRIPTION:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "description";
                currentField = new StringBuilder(extractValue(line));
            } else if (line.startsWith("TREATMENT_RECOMMENDATIONS:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "treatmentRecommendations";
                currentField = new StringBuilder(extractValue(line));
            } else if (line.startsWith("PESTICIDES:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "pesticides";
                currentField = new StringBuilder(extractValue(line));
            } else if (line.startsWith("PREVENTION_TIPS:")) {
                saveCurrentField(result, currentKey, currentField);
                currentKey = "preventionTips";
                currentField = new StringBuilder(extractValue(line));
            } else if (currentKey != null) {
                currentField.append(" ").append(line);
            }
        }

        saveCurrentField(result, currentKey, currentField);

        return result;
    }

    private void saveCurrentField(Map<String, String> result, String key, StringBuilder value) {
        if (key != null && value.length() > 0) {
            result.put(key, value.toString().trim());
        }
    }

    private String extractValue(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex == -1 || colonIndex + 1 >= line.length()) {
            return "";
        }
        return line.substring(colonIndex + 1).trim();
    }

    public Double parseConfidence(String confidenceStr) {
        try {
            String cleaned = confidenceStr.replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Boolean parseHasDisease(String hasDiseaseStr) {
        return hasDiseaseStr != null && hasDiseaseStr.toUpperCase().contains("YES");
    }

    public String getConfidenceFlag(Double confidence) {
        if (confidence >= 80) return "HIGH";
        if (confidence >= 50) return "MEDIUM";
        return "LOW";
    }
}