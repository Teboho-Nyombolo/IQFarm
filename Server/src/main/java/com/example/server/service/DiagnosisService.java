package com.example.server.service;

import com.example.server.dto.request.DiagnosisRequestDTO;
import com.example.server.dto.response.DiagnosisResponseDTO;

import java.util.List;

public interface DiagnosisService {

    DiagnosisResponseDTO analyzeAndSave(DiagnosisRequestDTO request);

    DiagnosisResponseDTO getDiagnosisById(Long diagnosisId);

    DiagnosisResponseDTO getDiagnosisByImageId(Long imageId);

    List<DiagnosisResponseDTO> getDiagnosesByTracker(Long trackerId);

    List<DiagnosisResponseDTO> getDiagnosesByCrop(Long cropId);

    List<DiagnosisResponseDTO> getAllDiagnoses();

    void deleteDiagnosis(Long diagnosisId);
}