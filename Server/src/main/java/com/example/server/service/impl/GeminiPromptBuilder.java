package com.example.server.service.impl;

import com.example.server.dto.request.DiagnosisRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class GeminiPromptBuilder {

    public String buildPrompt(DiagnosisRequestDTO request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert agricultural plant pathologist and crop consultant. Analyze the provided image of a plant/crop, and take into account the climate, soil profile, weather, and location to determine if it has a disease and provide comprehensive care recommendations.\n\n");

        if (request.getCropName() != null && !request.getCropName().isBlank()) {
            prompt.append("Crop/Plant Type: ").append(request.getCropName()).append("\n");
        }
        if (request.getPlantAge() != null && !request.getPlantAge().isBlank()) {
            prompt.append("Plant Age/Growth Stage: ").append(request.getPlantAge()).append("\n");
        }
        if (request.getSymptoms() != null && !request.getSymptoms().isBlank()) {
            prompt.append("Observed Symptoms: ").append(request.getSymptoms()).append("\n");
        }
        if (request.getWeatherConditions() != null && !request.getWeatherConditions().isBlank()) {
            prompt.append("Weather Conditions: ").append(request.getWeatherConditions()).append("\n");
        }
        if (request.getLocationRegion() != null && !request.getLocationRegion().isBlank()) {
            prompt.append("Location/Region: ").append(request.getLocationRegion()).append("\n");
        }
        if (request.getSoilType() != null && !request.getSoilType().isBlank()) {
            prompt.append("Soil Type: ").append(request.getSoilType()).append("\n");
        }
        if (request.getSoilHealth() != null && !request.getSoilHealth().isBlank()) {
            prompt.append("Soil Health: ").append(request.getSoilHealth()).append("\n");
        }
        if (request.getCurrentSeason() != null && !request.getCurrentSeason().isBlank()) {
            prompt.append("Current Season: ").append(request.getCurrentSeason()).append("\n");
        }

        prompt.append("\nPlease provide your analysis in the following EXACT format:\n\n");
        prompt.append("HAS_DISEASE: [YES or NO]\n");
        prompt.append("DISEASE_NAME: [Name of disease if YES, otherwise 'None']\n");
        prompt.append("CONFIDENCE_LEVEL: [Percentage 0-100]\n");
        prompt.append("DESCRIPTION: [Brief description of the disease and symptoms observed]\n");
        prompt.append("TREATMENT_RECOMMENDATIONS: [Step-by-step treatment instructions]\n");
        prompt.append("PESTICIDES: [Recommended pesticides/products to buy, with application instructions]\n");
        prompt.append("FERTILIZERS: [Recommended fertilizers and soil amendments, with application instructions]\n");
        prompt.append("PREVENTION_TIPS: [How to prevent this disease in the future]\n\n");
        prompt.append("Be specific, practical, and actionable. If you are uncertain, state your uncertainty clearly.");

        return prompt.toString();
    }
}