package com.example.server.service;

import com.example.server.dto.request.PlantAdvisoryRequestDTO;
import com.example.server.dto.response.PlantAdvisoryResponseDTO;

public interface PlantAdvisoryService {
    PlantAdvisoryResponseDTO getAdvisory(PlantAdvisoryRequestDTO request);
}
