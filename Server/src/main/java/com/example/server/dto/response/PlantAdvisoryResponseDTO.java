package com.example.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantAdvisoryResponseDTO {
    private String plantName;
    private String location;
    private String season;
    private String soilType;
    private String soilHealth;
    private String plantingAdvice;
    private String waterRequirements;
    private String fertilizerRecommendations;
    private String pestAndDiseaseTips;
    private String harvestTimingRecommendations;
    private String additionalRecommendations;
    private String warnings;
}
