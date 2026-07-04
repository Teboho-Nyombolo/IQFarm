package com.example.server.controller;

import com.example.server.dto.request.PlantAdvisoryRequestDTO;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.PlantAdvisoryResponseDTO;
import com.example.server.service.PlantAdvisoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/plant-advisory")
public class PlantAdvisoryController {

    private final PlantAdvisoryService plantAdvisoryService;

    public PlantAdvisoryController(PlantAdvisoryService plantAdvisoryService) {
        this.plantAdvisoryService = plantAdvisoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlantAdvisoryResponseDTO>> getAdvisory(@RequestBody PlantAdvisoryRequestDTO request) {
        PlantAdvisoryResponseDTO response = plantAdvisoryService.getAdvisory(request);
        return ResponseEntity.ok(ApiResponse.success("Plant advisory generated successfully", response));
    }
}
