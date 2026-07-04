package com.example.server.service;

import com.example.server.dto.request.CropRequestDTO;
import com.example.server.dto.response.CropResponseDTO;

import java.util.List;

public interface CropService {

    CropResponseDTO createCrop(CropRequestDTO request);

    CropResponseDTO getCropById(Long cropId);

    List<CropResponseDTO> getAllCrops();

    List<CropResponseDTO> getCropsByFarmId(Long farmId);

    CropResponseDTO updateCrop(Long cropId, CropRequestDTO request);

    void deleteCrop(Long cropId);

}
