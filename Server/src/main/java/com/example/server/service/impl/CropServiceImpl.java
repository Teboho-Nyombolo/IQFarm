package com.example.server.service.impl;

import com.example.server.dto.request.CropRequestDTO;
import com.example.server.dto.response.CropResponseDTO;
import com.example.server.mapper.CropMapper;
import com.example.server.repository.CropRepository;
import com.example.server.service.CropService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
//    private final FarmRepository farmRepository;
    private final CropMapper cropMapper;

    @Override
    public CropResponseDTO createCrop(CropRequestDTO request) {
        return null;
    }

    @Override
    public CropResponseDTO getCropById(Long cropId) {
        return null;
    }

    @Override
    public List<CropResponseDTO> getAllCrops() {
        return List.of();
    }

    @Override
    public List<CropResponseDTO> getCropsByFarmId(Long farmId) {
        return List.of();
    }

    @Override
    public CropResponseDTO updateCrop(Long cropId, CropRequestDTO request) {
        return null;
    }

    @Override
    public void deleteCrop(Long cropId) {

    }
}
