package com.helioroute.web.service;

import com.helioroute.web.dto.SolarResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
public class BackendClientService {

    // URL del microservicio de cálculo
    private static final String BACKEND_URL = "http://localhost:8083/api/solar/calculate";
    
    private final RestTemplate restTemplate;

    public BackendClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SolarResultDTO getSolarCalculation(double lat, double lon, LocalDateTime time, String strategy) {
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BACKEND_URL)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("time", time) // Spring Boot default serialization ISO works fine
                .queryParam("strategy", strategy);

        return restTemplate.getForObject(builder.toUriString(), SolarResultDTO.class);
    }
}