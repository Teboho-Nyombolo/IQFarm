package com.example.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropResponseDTO {

    private Long cropId;
    private Long farmId;
    private String farmName;
    private LocalDate cropDate;
    private String cropType;
    private String minerals;
    private String status;
    private Integer cropCount;
    private String waterFrequency;
    private String cropName;
}
