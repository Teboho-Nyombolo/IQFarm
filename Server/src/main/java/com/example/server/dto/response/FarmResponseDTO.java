package com.example.server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FarmResponseDTO {
    Long farmId;
    String farmName;
    String farmAddress;
    Double longitude;
    Double latitude;
}
