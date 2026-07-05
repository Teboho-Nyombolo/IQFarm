package com.example.server.dto.response;

import com.example.server.dto.DailyWeatherDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherResponseDTO {
    @Default
    public List<DailyWeatherDTO> dailyWeather = new ArrayList<DailyWeatherDTO>();

}
