package com.example.server.service.impl;

import com.example.server.client.GeminiClient;
import com.example.server.dto.request.PlantAdvisoryRequestDTO;
import com.example.server.dto.response.PlantAdvisoryResponseDTO;
import com.example.server.entity.Farm;
import com.example.server.repository.FarmRepository;
import com.example.server.service.PlantAdvisoryService;
import org.springframework.stereotype.Service;

@Service
public class PlantAdvisoryServiceImpl implements PlantAdvisoryService {

    private final GeminiClient geminiClient;
    private final PlantAdvisoryPromptBuilder promptBuilder;
    private final PlantAdvisoryResponseParser responseParser;
    private final FarmRepository farmRepository;

    public PlantAdvisoryServiceImpl(GeminiClient geminiClient,
                                    PlantAdvisoryPromptBuilder promptBuilder,
                                    PlantAdvisoryResponseParser responseParser,
                                    FarmRepository farmRepository) {
        this.geminiClient = geminiClient;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.farmRepository = farmRepository;
    }

    @Override
    public PlantAdvisoryResponseDTO getAdvisory(PlantAdvisoryRequestDTO request) {
        // If farmId is provided, try to get farm details
        if (request.getFarmId() != null) {
            Farm farm = farmRepository.findById(request.getFarmId()).orElse(null);
            if (farm != null) {
                if (request.getLocation() == null || request.getLocation().isBlank()) {
                    request.setLocation(farm.getLocation());
                }
            }
        }

        String prompt = promptBuilder.buildPrompt(request);
        String rawResponse = geminiClient.analyzeTextPrompt(prompt);
        return responseParser.parse(
                rawResponse,
                request.getPlantName(),
                request.getLocation(),
                request.getSeason(),
                request.getSoilType(),
                request.getSoilHealth()
        );
    }
}
