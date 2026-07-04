package com.example.server.service.impl;

import com.example.server.dto.request.PlantAdvisoryRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PlantAdvisoryPromptBuilder {

    public String buildPrompt(PlantAdvisoryRequestDTO request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert agricultural consultant specializing in African smallholder farming. ");
        prompt.append("Provide detailed, practical, actionable advice for growing the specified plant, ");
        prompt.append("considering the given context. Be specific and use local South African examples where relevant.\n\n");

        prompt.append("Plant to grow: ").append(request.getPlantName()).append("\n");
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            prompt.append("Location: ").append(request.getLocation()).append("\n");
        }
        if (request.getSeason() != null && !request.getSeason().isBlank()) {
            prompt.append("Season: ").append(request.getSeason()).append("\n");
        }
        if (request.getSoilType() != null && !request.getSoilType().isBlank()) {
            prompt.append("Soil type: ").append(request.getSoilType()).append("\n");
        }
        if (request.getSoilHealth() != null && !request.getSoilHealth().isBlank()) {
            prompt.append("Soil health: ").append(request.getSoilHealth()).append("\n");
        }

        prompt.append("\nPlease provide your advice in the following EXACT format:\n\n");
        prompt.append("PLANTING_ADVICE: [Practical planting instructions - when to plant, spacing, seed depth, etc.]\n");
        prompt.append("WATER_REQUIREMENTS: [Watering schedule and recommendations for this plant in the given conditions]\n");
        prompt.append("FERTILIZER_RECOMMENDATIONS: [Fertilizer recommendations (organic preferred if possible), application times and rates]\n");
        prompt.append("PEST_AND_DISEASE_TIPS: [Common pests/diseases for this plant in the area and how to manage/prevent them]\n");
        prompt.append("HARVEST_TIMING: [When to harvest and how to know it's ready]\n");
        prompt.append("ADDITIONAL_RECOMMENDATIONS: [Any other useful tips]\n");
        prompt.append("WARNINGS: [Any important warnings or potential issues to watch out for]\n\n");

        prompt.append("Make sure the advice is practical, affordable, and suitable for smallholder farmers.");

        return prompt.toString();
    }
}
