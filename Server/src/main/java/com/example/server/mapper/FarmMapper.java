package com.example.server.mapper;

import com.example.server.dto.request.FarmRequestDTO;
import com.example.server.dto.response.FarmResponseDTO;
import com.example.server.entity.Farm;
import com.example.server.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FarmMapper {

    //We need to pass after getting the long lat coordinates*********
    public FarmResponseDTO toResponseDTO(Farm request) {
        if(request == null)
            return null;

        return  FarmResponseDTO.builder()
                .farmId(request.getFarmId())
                .farmName(request.getFarmName())
                .farmAddress(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

    }

    public Farm toEntity(FarmRequestDTO request, User user) {
        if(request == null)
            return null;

        return  Farm.builder()
                .farmName(request.getFarmName())
                .farmOwner(user)
                .location(request.getFarmAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

    }
}
