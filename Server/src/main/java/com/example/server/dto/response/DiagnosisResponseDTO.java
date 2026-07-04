package com.example.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Boolean hasDisease;
    private String diseaseName;
    private Double confidenceLevel;
    private String confidenceFlag;
    private String description;
    private String treatmentRecommendations;
    private String pesticides;
    private String preventionTips;
    private LocalDateTime createdAt;
}
