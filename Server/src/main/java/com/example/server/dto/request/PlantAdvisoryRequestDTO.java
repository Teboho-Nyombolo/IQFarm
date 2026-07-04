package com.example.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantAdvisoryRequestDTO {
    private String plantName;
    private Long farmId; // Optional, to get location, weather, and soil info
    private String location;
    private String season;
    private String soilType;
    private String soilHealth;
}
