package com.example.server.service.impl;

import com.example.server.dto.request.CropRequestDTO;
import com.example.server.dto.response.CropResponseDTO;
import com.example.server.entity.Crop;
import com.example.server.entity.Farm;
import com.example.server.mapper.CropMapper;
import com.example.server.repository.CropRepository;
import com.example.server.repository.FarmRepository;
import com.example.server.service.CropService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final FarmRepository farmRepository;
    private final CropMapper cropMapper;

    public CropServiceImpl(CropRepository cropRepository, FarmRepository farmRepository, CropMapper cropMapper) {
        this.cropRepository = cropRepository;
        this.farmRepository = farmRepository;
        this.cropMapper = cropMapper;
    }

    @Override
    public CropResponseDTO createCrop(CropRequestDTO request) {
        Farm farm = farmRepository.findById(request.getFarmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + request.getFarmId()));

        Crop crop = cropMapper.toEntity(request, farm);
        Crop savedCrop = cropRepository.save(crop);

        return cropMapper.toResponse(savedCrop);
    }

    @Override
    public CropResponseDTO getCropById(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new EntityNotFoundException("Crop not found with id: " + cropId));
        return cropMapper.toResponse(crop);
    }

    @Override
    public List<CropResponseDTO> getAllCrops() {
        return cropRepository.findAll().stream()
                .map(cropMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropResponseDTO> getCropsByFarmId(Long farmId) {
        return cropRepository.findByFarm_FarmId(farmId).stream()
                .map(cropMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CropResponseDTO updateCrop(Long cropId, CropRequestDTO request) {
        Crop existingCrop = cropRepository.findById(cropId)
                .orElseThrow(() -> new EntityNotFoundException("Crop not found with id: " + cropId));

        Farm farm = farmRepository.findById(request.getFarmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + request.getFarmId()));

        cropMapper.updateEntityFromRequest(request, existingCrop, farm);
        Crop updatedCrop = cropRepository.save(existingCrop);

        return cropMapper.toResponse(updatedCrop);
    }

    @Override
    public void deleteCrop(Long cropId) {
        if (!cropRepository.existsById(cropId)) {
            throw new EntityNotFoundException("Crop not found with id: " + cropId);
        }
        cropRepository.deleteById(cropId);
    }
}
