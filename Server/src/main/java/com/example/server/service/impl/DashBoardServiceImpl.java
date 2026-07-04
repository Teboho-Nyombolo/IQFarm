package com.example.server.service.impl;

import com.example.server.dto.DailyWeatherDTO;
import com.example.server.dto.response.OpenMeteoResponse;
import com.example.server.dto.response.WeatherResponseDTO;
import com.example.server.entity.Farm;
import com.example.server.repository.FarmRepository;
import com.example.server.service.DashBoardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class DashBoardServiceImpl implements DashBoardService {

    private FarmRepository farmRepository;
    private final RestClient restClient;

    public DashBoardServiceImpl(FarmRepository farmRepository,  RestClient.Builder restClientBuilder) {
        this.farmRepository = farmRepository;
        this.restClient = restClientBuilder.baseUrl("https://api.open-meteo.com").build();
    }

    @Override
    public WeatherResponseDTO getCurrentWeather(Long ownerId) {
        Farm farm = this.farmRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + ownerId));

        System.out.println("************Found Farm*******************");
        System.out.println("farm: " + farm);
        System.out.println("************Farm Cords*******************");
        System.out.println("Lat: " + farm.getLongitude());
        System.out.println("Lon: " + farm.getLatitude());

        Double lon = farm.getLongitude();
        Double lat = farm.getLatitude();

        getWeatherByLocation(lat, lon);


        return getWeatherByLocation(lat, lon);
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
        System.out.println("**************Weather Data********************************");
        System.out.println("Weather Data: " + daily);

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
//    @Override
//    public SeasonResponseDTO getSeasonInsights(SeasonRequestDTO seasonRequestDTO) {
//        return null;
//    }
}
