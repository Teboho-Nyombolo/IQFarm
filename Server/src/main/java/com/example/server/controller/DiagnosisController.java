package com.example.server.controller;

import com.example.server.dto.request.DiagnosisRequestDTO;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.DiagnosisResponseDTO;
import com.example.server.service.DiagnosisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<DiagnosisResponseDTO>> analyze(@RequestBody DiagnosisRequestDTO request) {
        DiagnosisResponseDTO response = diagnosisService.analyzeAndSave(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DiagnosisResponseDTO>success("Diagnosis completed successfully", response));
    }

    @GetMapping("/{diagnosisId}")
    public ResponseEntity<ApiResponse<DiagnosisResponseDTO>> getDiagnosisById(@PathVariable Long diagnosisId) {
        DiagnosisResponseDTO response = diagnosisService.getDiagnosisById(diagnosisId);
        return ResponseEntity.ok(ApiResponse.<DiagnosisResponseDTO>success(response));
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<ApiResponse<DiagnosisResponseDTO>> getDiagnosisByImageId(@PathVariable Long imageId) {
        DiagnosisResponseDTO response = diagnosisService.getDiagnosisByImageId(imageId);
        return ResponseEntity.ok(ApiResponse.<DiagnosisResponseDTO>success(response));
    }

    @GetMapping("/tracker/{trackerId}")
    public ResponseEntity<ApiResponse<List<DiagnosisResponseDTO>>> getDiagnosesByTracker(@PathVariable Long trackerId) {
        List<DiagnosisResponseDTO> responses = diagnosisService.getDiagnosesByTracker(trackerId);
        return ResponseEntity.ok(ApiResponse.<List<DiagnosisResponseDTO>>success(responses));
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<ApiResponse<List<DiagnosisResponseDTO>>> getDiagnosesByCrop(@PathVariable Long cropId) {
        List<DiagnosisResponseDTO> responses = diagnosisService.getDiagnosesByCrop(cropId);
        return ResponseEntity.ok(ApiResponse.<List<DiagnosisResponseDTO>>success(responses));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiagnosisResponseDTO>>> getAllDiagnoses() {
        List<DiagnosisResponseDTO> responses = diagnosisService.getAllDiagnoses();
        return ResponseEntity.ok(ApiResponse.<List<DiagnosisResponseDTO>>success(responses));
    }

    @DeleteMapping("/{diagnosisId}")
    public ResponseEntity<ApiResponse<Void>> deleteDiagnosis(@PathVariable Long diagnosisId) {
        diagnosisService.deleteDiagnosis(diagnosisId);
        return ResponseEntity.ok(ApiResponse.<Void>success("Diagnosis deleted successfully", null));
    }
}