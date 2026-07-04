package com.example.server.dto.response;

import com.example.server.dto.DailyWeatherDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherResponseDTO {
    public List<DailyWeatherDTO> dailyWeather = new ArrayList<DailyWeatherDTO>();

}
