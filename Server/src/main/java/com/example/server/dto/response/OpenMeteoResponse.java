package com.example.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenMeteoResponse(
        double latitude,
        double longitude,
        @JsonProperty("daily") DailyData daily
) {
    public record DailyData(
            List<String> time,
            @JsonProperty("temperature_2m_max") List<Double> maxTemp,
            @JsonProperty("temperature_2m_min") List<Double> minTemp,
            @JsonProperty("precipitation_sum") List<Double> precipitation,
            @JsonProperty("wind_speed_10m_max") List<Double> windSpeed
    ) {}
}
