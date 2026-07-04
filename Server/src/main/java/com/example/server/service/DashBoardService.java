package com.example.server.service;

import com.example.server.dto.response.SeasonalResponseDTO;
import com.example.server.dto.response.WeatherResponseDTO;

public interface DashBoardService {

    WeatherResponseDTO getCurrentWeather(Long ownerId);

//    SoilResponseDTO getSoilConditionFromLatLang(SoilRequestFromLatLongDTO soilRequestFromLatLongDTO);
//
    SeasonalResponseDTO getCurrentSeasonal(Long farmId);
}
