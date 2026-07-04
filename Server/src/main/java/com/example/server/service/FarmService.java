package com.example.server.service;

import com.example.server.dto.request.FarmRequestDTO;
import com.example.server.dto.response.FarmResponseDTO;

public interface FarmService {

    FarmResponseDTO createFarm(FarmRequestDTO farmRequestDTO);
    FarmResponseDTO getFarmByOwnerId(Long ownerId);

}
