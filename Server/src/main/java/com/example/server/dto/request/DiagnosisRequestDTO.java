package com.example.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisRequestDTO {

    private Long imageId;
    private Long trackerId;
    private Long cropId;
    private String cropName;
    private String plantAge;
    private String symptoms;
    private String weatherConditions;
    private String locationRegion;

}
