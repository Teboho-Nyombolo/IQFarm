package com.example.server.controller;

import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.SeasonalResponseDTO;
import com.example.server.dto.response.WeatherResponseDTO;
import com.example.server.service.DashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("api/dashboard")
public class DashboardController {

    private final DashBoardService dashBoardService;

    public DashboardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/weather/{farmId}")
    public ResponseEntity<ApiResponse<WeatherResponseDTO>> getDailyWeather(@PathVariable Long farmId) {
        WeatherResponseDTO weatherResponseDTO = this.dashBoardService.getCurrentWeather(farmId);
        return ResponseEntity.ok(ApiResponse.success(weatherResponseDTO));
    }

    @GetMapping("/seasonal/{farmId}")
    public ResponseEntity<ApiResponse<SeasonalResponseDTO>> getSeasonal(@PathVariable Long farmId) {
        SeasonalResponseDTO seasonalResponseDTO = this.dashBoardService.getCurrentSeasonal(farmId);
        return ResponseEntity.ok(ApiResponse.success(seasonalResponseDTO));
    }
}
