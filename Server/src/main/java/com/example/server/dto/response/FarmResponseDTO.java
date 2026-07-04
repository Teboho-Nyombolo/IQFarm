package com.example.server.dto.response;

import com.example.server.entity.Crop;
import com.example.server.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FarmResponseDTO {
    User farmOwner;
    String farmName;
    String farmAddress;
    Double longitude;
    Double latitude;
    List<Crop> crops;
}
