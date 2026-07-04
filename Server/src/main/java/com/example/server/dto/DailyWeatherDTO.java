package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record DailyWeatherDTO(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @JsonProperty("highTemp")
        double highTemperature,

        @JsonProperty("lowTemp")
        double lowTemperature,

        double precipitation,
        double windSpeed,
        int humidity,
        String condition
) {}
