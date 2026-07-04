package com.example.server.controller;

import com.example.server.dto.request.FarmRequestDTO;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.FarmResponseDTO;
import com.example.server.service.FarmService;
import com.example.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("/api/farm")

public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FarmResponseDTO>> createFarm(@RequestBody FarmRequestDTO farmRequestDTO) {
        FarmResponseDTO farmResponseDTO = this.farmService.createFarm(farmRequestDTO);
        return ResponseEntity.ok(ApiResponse.success(farmResponseDTO));
    }
}
