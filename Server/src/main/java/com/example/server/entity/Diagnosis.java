package com.example.server.entity;

import java.time.LocalDateTime;

import com.example.server.enums.ConfidenceFlag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long diagnosisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracker_id")
    private CropTracker cropTracker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "plant_age")
    private String plantAge;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "weather_conditions")
    private String weatherConditions;

    @Column(name = "location_region")
    private String locationRegion;

    @Column(name = "soil_type")
    private String soilType;

    @Column(name = "soil_health")
    private String soilHealth;

    @Column(name = "current_season")
    private String currentSeason;

    @Column(name = "has_disease", nullable = false)
    private Boolean hasDisease;

    @Column(name = "disease_name")
    private String diseaseName;

    @Column(name = "confidence_level")
    private Double confidenceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_flag")
    private ConfidenceFlag confidenceFlag; // "HIGH", "MEDIUM", "LOW"

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "treatment_recommendations", columnDefinition = "TEXT")
    private String treatmentRecommendations;

    @Column(name = "pesticides", columnDefinition = "TEXT")
    private String pesticides;

    @Column(name = "fertilizers", columnDefinition = "TEXT")
    private String fertilizers;

    @Column(name = "prevention_tips", columnDefinition = "TEXT")
    private String preventionTips;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "weather_impact", columnDefinition = "TEXT")
    private String weatherImpact;

    @Column(name = "soil_impact", columnDefinition = "TEXT")
    private String soilImpact;

    @Column(name = "pest_impact", columnDefinition = "TEXT")
    private String pestImpact;

    @Column(name = "pesticide_impact", columnDefinition = "TEXT")
    private String pesticideImpact;

    @Column(name = "yield_rescue_measures", columnDefinition = "TEXT")
    private String yieldRescueMeasures;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
