package com.example.server.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResponseDTO {

    private Long diagnosisId;
    private Long imageId;
    private String imageUrl;
    private Long trackerId;
    private Long cropId;
    private String cropName;
    private String plantAge;
    private String symptoms;
    private String weatherConditions;
    private String locationRegion;
    private String soilType;
    private String soilHealth;
    private String currentSeason;
    private Boolean hasDisease;
    private String diseaseName;
    private Double confidenceLevel;
    private String confidenceFlag;
    private String description;
    private String treatmentRecommendations;
    private String pesticides;
    private String fertilizers;
    private String preventionTips;
    private String rootCause;
    private String weatherImpact;
    private String soilImpact;
    private String pestImpact;
    private String pesticideImpact;
    private String yieldRescueMeasures;
    private LocalDateTime createdAt;
}
