package com.example.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "crops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crop_id")
    private Long cropId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(name = "crop_date")
    private LocalDate cropDate;

    @Column(name = "crop_type", nullable = false)
    private String cropType;

    private String minerals;

    private String status;

    @Column(name = "crop_count")
    private Integer cropCount;

    @Column(name = "water_frequency")
    private String waterFrequency;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @OneToMany(mappedBy = "crop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CropTracker> cropTrackers;
}
