package com.example.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropRequestDTO {

    private Long farmId;
    private LocalDate cropDate;
    private String cropType;
    private String minerals;
    private String status;
    private Integer cropCount;
    private String waterFrequency;
    private String cropName;

}
