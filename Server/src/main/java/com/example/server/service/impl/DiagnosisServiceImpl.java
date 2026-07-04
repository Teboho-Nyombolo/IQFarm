package com.example.server.service.impl;

import com.example.server.client.GeminiClient;
import com.example.server.dto.request.DiagnosisRequestDTO;
import com.example.server.dto.response.DiagnosisResponseDTO;
import com.example.server.entity.Crop;
import com.example.server.entity.CropTracker;
import com.example.server.entity.Diagnosis;
import com.example.server.entity.Image;
import com.example.server.repository.CropRepository;
import com.example.server.repository.CropTrackerRepository;
import com.example.server.repository.DiagnosisRepository;
import com.example.server.repository.ImageRepository;
import com.example.server.service.DiagnosisService;
import com.example.server.service.GeminiPromptBuilder;
import com.example.server.service.GeminiResponseParser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final ImageRepository imageRepository;
    private final CropTrackerRepository cropTrackerRepository;
    private final CropRepository cropRepository;
    private final GeminiClient geminiClient;
    private final GeminiPromptBuilder promptBuilder;
    private final GeminiResponseParser responseParser;

    @Override
    public DiagnosisResponseDTO analyzeAndSave(DiagnosisRequestDTO request) {
        Image image = imageRepository.findById(request.getImageId())
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + request.getImageId()));

        String prompt = promptBuilder.buildPrompt(request);

        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(Paths.get(image.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file: " + image.getFilePath(), e);
        }

        String rawResponse = geminiClient.analyzePlantDisease(imageBytes, image.getMimeType(), prompt);

        Map<String, String> parsed = responseParser.parse(rawResponse);
        Double confidence = responseParser.parseConfidence(parsed.get("confidenceLevel"));
        Boolean hasDisease = responseParser.parseHasDisease(parsed.get("hasDisease"));
        String confidenceFlag = responseParser.getConfidenceFlag(confidence);

        Diagnosis.DiagnosisBuilder diagnosisBuilder = Diagnosis.builder()
                .image(image)
                .cropName(request.getCropName())
                .plantAge(request.getPlantAge())
                .symptoms(request.getSymptoms())
                .weatherConditions(request.getWeatherConditions())
                .locationRegion(request.getLocationRegion())
                .hasDisease(hasDisease)
                .diseaseName(parsed.get("diseaseName"))
                .confidenceLevel(confidence)
                .confidenceFlag(confidenceFlag)
                .description(parsed.get("description"))
                .treatmentRecommendations(parsed.get("treatmentRecommendations"))
                .pesticides(parsed.get("pesticides"))
                .preventionTips(parsed.get("preventionTips"))
                .rawResponse(rawResponse);

        if (request.getTrackerId() != null) {
            CropTracker tracker = cropTrackerRepository.findById(request.getTrackerId())
                    .orElseThrow(() -> new EntityNotFoundException("Tracker not found with id: " + request.getTrackerId()));
            diagnosisBuilder.cropTracker(tracker);
        }

        if (request.getCropId() != null) {
            Crop crop = cropRepository.findById(request.getCropId())
                    .orElseThrow(() -> new EntityNotFoundException("Crop not found with id: " + request.getCropId()));
            diagnosisBuilder.crop(crop);
        }

        Diagnosis saved = diagnosisRepository.save(diagnosisBuilder.build());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisResponseDTO getDiagnosisById(Long diagnosisId) {
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found with id: " + diagnosisId));
        return toResponse(diagnosis);
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisResponseDTO getDiagnosisByImageId(Long imageId) {
        Diagnosis diagnosis = diagnosisRepository.findByImage_ImageId(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found for image id: " + imageId));
        return toResponse(diagnosis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponseDTO> getDiagnosesByTracker(Long trackerId) {
        return diagnosisRepository.findByCropTracker_TrackerId(trackerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponseDTO> getDiagnosesByCrop(Long cropId) {
        return diagnosisRepository.findByCrop_CropId(cropId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponseDTO> getAllDiagnoses() {
        return diagnosisRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDiagnosis(Long diagnosisId) {
        if (!diagnosisRepository.existsById(diagnosisId)) {
            throw new EntityNotFoundException("Diagnosis not found with id: " + diagnosisId);
        }
        diagnosisRepository.deleteById(diagnosisId);
    }

    private DiagnosisResponseDTO toResponse(Diagnosis d) {
        return DiagnosisResponseDTO.builder()
                .diagnosisId(d.getDiagnosisId())
                .imageId(d.getImage().getImageId())
                .imageUrl("http://localhost:8080/uploads/images/" + d.getImage().getStoredName())
                .trackerId(d.getCropTracker() != null ? d.getCropTracker().getTrackerId() : null)
                .cropId(d.getCrop() != null ? d.getCrop().getCropId() : null)
                .cropName(d.getCropName())
                .plantAge(d.getPlantAge())
                .symptoms(d.getSymptoms())
                .weatherConditions(d.getWeatherConditions())
                .locationRegion(d.getLocationRegion())
                .hasDisease(d.getHasDisease())
                .diseaseName(d.getDiseaseName())
                .confidenceLevel(d.getConfidenceLevel())
                .confidenceFlag(d.getConfidenceFlag())
                .description(d.getDescription())
                .treatmentRecommendations(d.getTreatmentRecommendations())
                .pesticides(d.getPesticides())
                .preventionTips(d.getPreventionTips())
                .createdAt(d.getCreatedAt())
                .build();
    }
}