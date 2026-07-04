package com.example.server.dto.response;

import java.util.List;

public record SeasonalResponseDTO(
        String geologicalArea,
        String currentSeason,
        String climateSummary,
        SoilProfile soilProfile,
        List<CropRecommendation> recommendedCrops
) {
    public record SoilProfile(
            String texture, // e.g., Loamy, Sandy, Clay
            String optimalPhRange,
            List<String> primaryNutrientStatus // e.g., ["Nitrogen: Medium", "Phosphorus: Low"]
    ) {}

    public record CropRecommendation(
            String cropName,
            String optimalPlantingWindow,
            int estimatedDaysToHarvest,
            String waterRequirement, // e.g., Low, Moderate, High
            List<String> companionCrops,
            String growthInsight
    ) {}
}

