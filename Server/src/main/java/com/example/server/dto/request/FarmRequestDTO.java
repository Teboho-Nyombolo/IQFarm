package com.example.server.dto.request;

import com.example.server.entity.User;
import lombok.Data;

@Data
public class FarmRequestDTO {
    Long ownerId;
    String farmName;
    String farmAddress;
    Double longitude;
    Double latitude;
}
