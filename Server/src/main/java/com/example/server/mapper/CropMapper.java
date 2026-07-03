package com.example.server.mapper;

import com.example.server.dto.request.CropRequestDTO;
import com.example.server.dto.response.CropResponseDTO;
import com.example.server.entity.Crop;
import com.example.server.entity.Farm;
import org.springframework.stereotype.Component;

@Component
public class CropMapper {


    public Crop toEntity(CropRequestDTO request, Farm farm) {
        return Crop.builder()
                .farm(farm)
                .cropDate(request.getCropDate())
                .cropType(request.getCropType())
                .minerals(request.getMinerals())
                .status(request.getStatus())
                .cropCount(request.getCropCount())
                .waterFrequency(request.getWaterFrequency())
                .cropName(request.getCropName())
                .build();
    }

    public CropResponseDTO toResponse(Crop crop) {
        return CropResponseDTO.builder()
                .cropId(crop.getCropId())
                .farmId(crop.getFarm().getFarmId())
                .farmName(crop.getFarm().getFarmName())
                .cropDate(crop.getCropDate())
                .cropType(crop.getCropType())
                .minerals(crop.getMinerals())
                .status(crop.getStatus())
                .cropCount(crop.getCropCount())
                .waterFrequency(crop.getWaterFrequency())
                .cropName(crop.getCropName())
                .build();
    }

    public void updateEntityFromRequest(CropRequestDTO request, Crop crop, Farm farm) {
        crop.setFarm(farm);
        crop.setCropDate(request.getCropDate());
        crop.setCropType(request.getCropType());
        crop.setMinerals(request.getMinerals());
        crop.setStatus(request.getStatus());
        crop.setCropCount(request.getCropCount());
        crop.setWaterFrequency(request.getWaterFrequency());
        crop.setCropName(request.getCropName());
    }
}
