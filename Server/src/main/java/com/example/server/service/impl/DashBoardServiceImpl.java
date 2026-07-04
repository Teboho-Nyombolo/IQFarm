package com.example.server.service.impl;

import com.example.server.dto.DailyWeatherDTO;
import com.example.server.dto.response.OpenMeteoResponse;
import com.example.server.dto.response.SeasonalResponseDTO;
import com.example.server.dto.response.WeatherResponseDTO;
import com.example.server.entity.Farm;
import com.example.server.repository.FarmRepository;
import com.example.server.service.DashBoardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashBoardServiceImpl implements DashBoardService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private FarmRepository farmRepository;
    private final RestClient restClient;
    private final RestClient restClient2;
    private final ObjectMapper objectMapper;

    public DashBoardServiceImpl(FarmRepository farmRepository,  RestClient.Builder restClientBuilder,
                                RestClient.Builder restClient2Builder, ObjectMapper objectMapper) {
        this.farmRepository = farmRepository;
        this.restClient = restClientBuilder.baseUrl("https://api.open-meteo.com").build();
        this.restClient2 = restClient2Builder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.objectMapper = objectMapper;
    }

    public SeasonalResponseDTO getSeasonalCropInsights(String location, String coordinates) {
        // Build a dynamic, production-safe prompt message context
        String userPrompt = String.format(
                "Analyze the agricultural environment for location: %s (Coordinates: %s). " +
                        "Provide current seasonal data and targeted insights identifying the most suitable crops for planting.",
                location, coordinates
        );

        // Define your strict JSON Schema (Same generationConfig configuration from earlier step)
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "OBJECT",
                                "properties", Map.of(
                                        "geologicalArea", Map.of("type", "STRING"),
                                        "currentSeason", Map.of("type", "STRING"),
                                        "climateSummary", Map.of("type", "STRING"),
                                        "soilProfile", Map.of(
                                                "type", "OBJECT",
                                                "properties", Map.of(
                                                        "texture", Map.of("type", "STRING"),
                                                        "optimalPhRange", Map.of("type", "STRING"),
                                                        "primaryNutrientStatus", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"))
                                                ),
                                                "required", List.of("texture", "optimalPhRange", "primaryNutrientStatus")
                                        ),
                                        "recommendedCrops", Map.of(
                                                "type", "ARRAY",
                                                "items", Map.of(
                                                        "type", "OBJECT",
                                                        "properties", Map.of(
                                                                "cropName", Map.of("type", "STRING"),
                                                                "optimalPlantingWindow", Map.of("type", "STRING"),
                                                                "estimatedDaysToHarvest", Map.of("type", "INTEGER"),
                                                                "waterRequirement", Map.of("type", "STRING"),
                                                                "companionCrops", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
                                                                "growthInsight", Map.of("type", "STRING")
                                                        ),
                                                        "required", List.of("cropName", "optimalPlantingWindow", "estimatedDaysToHarvest", "waterRequirement", "companionCrops", "growthInsight")
                                                )
                                        )
                                ),
                                "required", List.of("geologicalArea", "currentSeason", "climateSummary", "soilProfile", "recommendedCrops")
                        )
                )
        );

        try {
            // 1. Fetch the response as a generic JSON node to read Google's metadata wrapper
            JsonNode rawEnvelope = restClient2.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (rawEnvelope == null || !rawEnvelope.has("candidates")) {
                throw new RuntimeException("Empty response envelope from Gemini API");
            }

            // 2. Drill directly into the candidate token payload to extract the text string
            String pureJsonString = rawEnvelope
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // 3. Explicitly transform the internal clean JSON string into your Java record
            return objectMapper.readValue(pureJsonString, SeasonalResponseDTO.class);

        } catch (Exception ex) {
            System.err.println("API parsing failed, deploying fallback: " + ex.getMessage());
            return getMockFallbackData(location); // Safe local fallback object
        }
    }

    public SeasonalResponseDTO getMockFallbackData(String location){
        var soil = new SeasonalResponseDTO.SoilProfile(
                "Loamy / Clay Mix",
                "6.0 - 7.0",
                java.util.List.of("Nitrogen: Medium", "Phosphorus: Moderate", "Potassium: High")
        );

        var crop1 = new SeasonalResponseDTO.CropRecommendation(
                "Maize (Corn)",
                "Early Spring to Mid Summer",
                120,
                "Moderate",
                java.util.List.of("Beans", "Squash"),
                "Ensure deep watering during early tasseling stages. Best suited for current regional warmth."
        );

        var crop2 = new SeasonalResponseDTO.CropRecommendation(
                "Legumes (Beans)",
                "Spring onwards",
                90,
                "Low",
                java.util.List.of("Maize", "Potatoes"),
                "Excellent nitrogen-fixing plant that improves soil quality for subsequent crop rotations."
        );

        return new SeasonalResponseDTO(
                location,
                "Active Growing Season",
                "Stable temperatures with moderate atmospheric moisture indicators.",
                soil,
                java.util.List.of(crop1, crop2)
        );
    }

    @Override
    public WeatherResponseDTO getCurrentWeather(Long ownerId) {
        List<String> latlon = getLatLongAddress(ownerId);
        Double lon = Double.parseDouble(latlon.get(0));
        Double lat = Double.parseDouble(latlon.get(1));

//        getWeatherByLocation(lat, lon);

        return getWeatherByLocation(lat, lon);
    }

    public List<String> getLatLongAddress(Long id){
        Farm farm = this.farmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + id));

        List<String> latLon = new ArrayList<>();
        latLon.add(farm.getLatitude().toString());
        latLon.add(farm.getLongitude().toString());
        latLon.add(farm.getLocation());
        return latLon;
    }

    public WeatherResponseDTO getWeatherByLocation(double lat, double lon) {
        // Fetch raw weather details from the external API
        OpenMeteoResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max")
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);

        if (response == null || response.daily() == null || response.daily().time().isEmpty()) {
            throw new RuntimeException("Failed to fetch weather data");
        }

        // Map the first element of the arrays into your clean DailyWeatherDto structure
        var daily = response.daily();

        WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO();
        for (int i = 0; i < 5; i++) {
            weatherResponseDTO.dailyWeather.add(new DailyWeatherDTO(
                    LocalDate.parse(daily.time().get(0)),
                    daily.maxTemp().get(0),
                    daily.minTemp().get(0),
                    daily.precipitation().get(0),
                    daily.windSpeed().get(0),
                    75,               // Placeholder as daily relative humidity requires hourly aggregation
                    "Sunny/Cloudy"    // Placeholder for text conditions
            ));
        }
        return weatherResponseDTO;
    }

//    @Override
//    public SoilResponseDTO getSoilConditionFromLatLang(SoilRequestFromLatLongDTO soilRequestFromLatLongDTO) {
//        return null;
//    }
//
    @Override
    public SeasonalResponseDTO getCurrentSeasonal(Long farmId) {
        List<String> longLatAdd =  getLatLongAddress(farmId);
        String cords = longLatAdd.get(0) + ", " + longLatAdd.get(1);
        String address = longLatAdd.get(2);
        getSeasonalCropInsights(address, cords);
        return getSeasonalCropInsights(address, cords);
    }
}
