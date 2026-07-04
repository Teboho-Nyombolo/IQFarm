package com.example.server.controller;

import com.example.server.dto.request.CropTrackerRequestDTO;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.CropTrackerResponseDTO;
import com.example.server.enums.LogType;
import com.example.server.enums.Status;
import com.example.server.service.CropTrackerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("/api/crop-trackers")
public class CropTrackerController {


    private final CropTrackerService cropTrackerService;

    public CropTrackerController(CropTrackerService cropTrackerService) {
        this.cropTrackerService = cropTrackerService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CropTrackerResponseDTO>> createTracker(@RequestBody CropTrackerRequestDTO request) {
        CropTrackerResponseDTO response = cropTrackerService.createTracker(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CropTracker created successfully", response));
    }

    @GetMapping("/{trackerId}")
    public ResponseEntity<ApiResponse<CropTrackerResponseDTO>> getTrackerById(@PathVariable Long trackerId) {
        CropTrackerResponseDTO response = cropTrackerService.getTrackerById(trackerId);
        return ResponseEntity.ok(ApiResponse.success("Tracked Crop", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CropTrackerResponseDTO>>> getAllTrackers() {
        List<CropTrackerResponseDTO> responses = cropTrackerService.getAllTrackers();
        return ResponseEntity.ok(ApiResponse.success("All Tracked Crops", responses));
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<ApiResponse<List<CropTrackerResponseDTO>>> getTrackersByCropId(@PathVariable Long cropId) {
        List<CropTrackerResponseDTO> responses = cropTrackerService.getTrackersByCropId(cropId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CropTrackerResponseDTO>>> getTrackersByStatus(@PathVariable Status status) {
        List<CropTrackerResponseDTO> responses = cropTrackerService.getTrackersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Crops tracked by status", responses));
    }

    @GetMapping("/log-type/{logType}")
    public ResponseEntity<ApiResponse<List<CropTrackerResponseDTO>>> getTrackersByLogType(@PathVariable LogType logType) {
        List<CropTrackerResponseDTO> responses = cropTrackerService.getTrackersByLogType(logType);
        return ResponseEntity.ok(ApiResponse.success("Types", responses));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<CropTrackerResponseDTO>>> getTrackersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CropTrackerResponseDTO> responses = cropTrackerService.getTrackersByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Date range", responses));
    }

    @PutMapping("/{trackerId}")
    public ResponseEntity<ApiResponse<CropTrackerResponseDTO>> updateTracker(
            @PathVariable Long trackerId,
            @RequestBody CropTrackerRequestDTO request) {
        CropTrackerResponseDTO response = cropTrackerService.updateTracker(trackerId, request);
        return ResponseEntity.ok(ApiResponse.success("CropTracker updated successfully", response));
    }

    @DeleteMapping("/{trackerId}")
    public ResponseEntity<ApiResponse<Void>> deleteTracker(@PathVariable Long trackerId) {
        cropTrackerService.deleteTracker(trackerId);
        return ResponseEntity.ok(ApiResponse.success("CropTracker deleted successfully", null));
    }
}
