package com.example.server.service.impl;

import com.example.server.dto.response.PlantAdvisoryResponseDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PlantAdvisoryResponseParser {

    public PlantAdvisoryResponseDTO parse(String rawResponse, String plantName, String location, String season, String soilType, String soilHealth) {
        Map<String, String> fields = parseFields(rawResponse);

        return PlantAdvisoryResponseDTO.builder()
                .plantName(plantName)
                .location(location)
                .season(season)
                .soilType(soilType)
                .soilHealth(soilHealth)
                .plantingAdvice(fields.getOrDefault("plantingAdvice", ""))
                .waterRequirements(fields.getOrDefault("waterRequirements", ""))
                .fertilizerRecommendations(fields.getOrDefault("fertilizerRecommendations", ""))
                .pestAndDiseaseTips(fields.getOrDefault("pestAndDiseaseTips", ""))
                .harvestTimingRecommendations(fields.getOrDefault("harvestTiming", ""))
                .additionalRecommendations(fields.getOrDefault("additionalRecommendations", ""))
                .warnings(fields.getOrDefault("warnings", ""))
                .build();
    }

    private Map<String, String> parseFields(String rawResponse) {
        Map<String, String> result = new HashMap<>();
        String[] lines = rawResponse.split("\n");

        String currentKey = null;
        StringBuilder currentValue = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("PLANTING_ADVICE:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "plantingAdvice";
                currentValue = new StringBuilder(extractValue(line));
            } else if (line.startsWith("WATER_REQUIREMENTS:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "waterRequirements";
                currentValue = new StringBuilder(extractValue(line));
            } else if (line.startsWith("FERTILIZER_RECOMMENDATIONS:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "fertilizerRecommendations";
                currentValue = new StringBuilder(extractValue(line));
            } else if (line.startsWith("PEST_AND_DISEASE_TIPS:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "pestAndDiseaseTips";
                currentValue = new StringBuilder(extractValue(line));
            } else if (line.startsWith("HARVEST_TIMING:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "harvestTiming";
                currentValue = new StringBuilder(extractValue(line));
            } else if (line.startsWith("ADDITIONAL_RECOMMENDATIONS:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "additionalRecommendations";
                currentValue = new StringBuilder(extractValue(line));
            } else if (line.startsWith("WARNINGS:")) {
                saveField(result, currentKey, currentValue);
                currentKey = "warnings";
                currentValue = new StringBuilder(extractValue(line));
            } else if (currentKey != null) {
                if (currentValue.length() > 0) {
                    currentValue.append(" ");
                }
                currentValue.append(line);
            }
        }
        saveField(result, currentKey, currentValue);

        return result;
    }

    private void saveField(Map<String, String> result, String key, StringBuilder value) {
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
}
