package com.example.server.service;

import com.example.server.dto.response.WeatherResponseDTO;


public interface DashBoardService {

    WeatherResponseDTO getCurrentWeather(Long ownerId);

//    SoilResponseDTO getSoilConditionFromLatLang(SoilRequestFromLatLongDTO soilRequestFromLatLongDTO);
//
//    SeasonResponseDTO getSeasonInsights(SeasonRequestDTO seasonRequestDTO);
}
