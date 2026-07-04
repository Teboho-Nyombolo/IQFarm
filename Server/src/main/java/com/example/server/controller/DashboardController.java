package com.example.server.controller;

import com.example.server.dto.response.ApiResponse;
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

    @GetMapping("/{ownerId}")
    public ResponseEntity<ApiResponse<WeatherResponseDTO>> getDailyWeather(@PathVariable Long ownerId) {
        WeatherResponseDTO weatherResponseDTO = this.dashBoardService.getCurrentWeather(ownerId);
        return ResponseEntity.ok(ApiResponse.success(weatherResponseDTO));
    }
}
