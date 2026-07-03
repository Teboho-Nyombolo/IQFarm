package com.example.server.controller;

import com.example.server.dto.request.CropRequestDTO;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.CropResponseDTO;
import com.example.server.service.CropService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CropResponseDTO>> createCrop(@RequestBody CropRequestDTO request) {
        CropResponseDTO response = cropService.createCrop(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Crop created successfully", response));
    }

    @GetMapping("/{cropId}")
    public ResponseEntity<ApiResponse<CropResponseDTO>> getCropById(@PathVariable Long cropId) {
        CropResponseDTO response = cropService.getCropById(cropId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CropResponseDTO>>> getAllCrops() {
        List<CropResponseDTO> responses = cropService.getAllCrops();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<ApiResponse<List<CropResponseDTO>>> getCropsByFarmId(@PathVariable Long farmId) {
        List<CropResponseDTO> responses = cropService.getCropsByFarmId(farmId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{cropId}")
    public ResponseEntity<ApiResponse<CropResponseDTO>> updateCrop(
            @PathVariable Long cropId,
            @RequestBody CropRequestDTO request) {
        CropResponseDTO response = cropService.updateCrop(cropId, request);
        return ResponseEntity.ok(ApiResponse.success("Crop updated successfully", response));
    }

    @DeleteMapping("/{cropId}")
    public ResponseEntity<ApiResponse<Void>> deleteCrop(@PathVariable Long cropId) {
        cropService.deleteCrop(cropId);
        return ResponseEntity.ok(ApiResponse.success("Crop deleted successfully", null));
    }
}
