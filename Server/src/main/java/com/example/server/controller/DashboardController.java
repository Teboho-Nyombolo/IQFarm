package com.example.server.controller;

import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.SeasonalResponseDTO;
import com.example.server.dto.response.WeatherResponseDTO;
import com.example.server.service.DashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dashboard")
public class DashboardController {

    private final DashBoardService dashBoardService;

    public DashboardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/weather/{ownerId}")
    public ResponseEntity<ApiResponse<WeatherResponseDTO>> getDailyWeather(@PathVariable Long ownerId) {
        WeatherResponseDTO weatherResponseDTO = this.dashBoardService.getCurrentWeather(ownerId);
        return ResponseEntity.ok(ApiResponse.success(weatherResponseDTO));
    }

    @GetMapping("/seasonal/{ownerId}")
    public ResponseEntity<ApiResponse<SeasonalResponseDTO>> getSeasonal(@PathVariable Long ownerId) {
        SeasonalResponseDTO seasonalResponseDTO = this.dashBoardService.getCurrentSeasonal(ownerId);
        return ResponseEntity.ok(ApiResponse.success(seasonalResponseDTO));
    }
}
