package com.helioroute.calculation.patterns.adapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// ADAPTEE: Librería real que conecta con Open-Meteo API (Forecast)
// Ahora incluimos Elevation + Temperature + Wind
public class ExternalTopoLibrary {
    
    // URL Base de Open-Meteo Forecast que unifica todo
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";
    private final HttpClient httpClient;

    public ExternalTopoLibrary() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String fetchCombinedData(double lat, double lon) {
        System.out.println("Llamando a Open-Meteo Forecast API real para: " + lat + ", " + lon);
        
        try {
            // Updated Professional Query: Daily sunrise/sunset, Hourly temp/wind/uv + Cloud/Vis/Freezing
            // Added current=pressure_msl
            // Added hourly=freezing_level_height,visibility,snow_depth,pressure_msl
            String uriStr = String.format(java.util.Locale.US, 
                "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,wind_speed_10m,weather_code,is_day,surface_pressure&hourly=temperature_2m,wind_speed_10m,uv_index,is_day,freezing_level_height,visibility,snow_depth&daily=sunrise,sunset&forecast_days=1&timezone=auto", 
                API_URL, lat, lon);
            
            // REMOVED: uriStr.replace(',', '.') - This was breaking the API parameters!
            // String.format with Locale.US ensures coordinates use dots.
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uriStr))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "ERROR: Status code " + response.statusCode();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; 
        }
    }
}