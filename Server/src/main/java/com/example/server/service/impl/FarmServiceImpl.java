package com.example.server.service.impl;

import com.example.server.dto.request.FarmRequestDTO;
import com.example.server.dto.response.FarmResponseDTO;
import com.example.server.entity.Farm;
import com.example.server.entity.User;
import com.example.server.mapper.FarmMapper;
import com.example.server.repository.FarmRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.FarmService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.Optional;

@Service

public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;
    private final FarmMapper farmMapper;

    private static final String API_KEY = "0f3de5dba5474637bd83aeeb31dc3e70";

    public FarmServiceImpl(FarmRepository farmRepository, UserRepository userRepository, FarmMapper farmMapper) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
        this.farmMapper = farmMapper;
    }

    @Override
    public FarmResponseDTO createFarm(FarmRequestDTO farmRequestDTO) {

        if(!checkUser(farmRequestDTO.getOwnerId())){
            return null;
        }

        User owner = this.userRepository.findById(farmRequestDTO.getOwnerId()).get();

        String address = farmRequestDTO.getFarmAddress();

        List<Double> longLat = getFarmLongLat(address);

        farmRequestDTO.setLongitude(longLat.get(0));
        farmRequestDTO.setLatitude(longLat.get(1));

        Farm farm = farmMapper.toEntity(farmRequestDTO, owner);

        this.farmRepository.save(farm);

        return farmMapper.toResponseDTO(farm);
    }

    @Override
    public FarmResponseDTO getFarmByOwnerId(Long ownerId) {
        Optional<Farm> farm = farmRepository.findByFarmOwner_UserId(ownerId);
        return farm.map(farmMapper::toResponseDTO).orElse(null);
    }

    public boolean checkUser(Long ownerId){
        User user = userRepository.findById(ownerId).orElse(null);
        return user != null;
    }

    public List<Double> getFarmLongLat(String address) {

        List<Double> longLat = new ArrayList<>();

        try {
            // 1. URL encode the address string
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());

            // 2. Build URL with a country filter strict to South Africa (countrycode:za)
            String urlString = "https://api.geoapify.com/v1/geocode/search?text=" + encodedAddress
                    + "&filter=countrycode:za"
                    + "&apiKey=" + API_KEY;

            System.out.println("*******************URL STRING********************");
            System.out.println("URL STRING: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();

                // 3. Extract Coordinates using regex
                String lon = getJsonValue(jsonResponse, "lon");
                String lat = getJsonValue(jsonResponse, "lat");

                System.out.println("***************************Longitude and Latitude********************");
                System.out.println("Longitude: " + lon);
                System.out.println("Latitude: " + lat);

                longLat.add(Double.parseDouble(lon));
                longLat.add(Double.parseDouble(lat));

                System.out.println("SA Address: " + address);
                System.out.println("Latitude:   " + lat);
                System.out.println("Longitude:  " + lon);

            } else if (responseCode == 429) {
                System.out.println("Error 429: Daily free limit of 3,000 requests exceeded.");
            } else {
                System.out.println("Error: HTTP Server responded with code " + responseCode);
            }

            return longLat;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\":\\s*(-?\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Not Found";
    }

}
