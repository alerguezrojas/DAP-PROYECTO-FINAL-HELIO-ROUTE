package com.helioroute.calculation.patterns.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helioroute.calculation.domain.EnvironmentalData;
import com.helioroute.calculation.domain.HourlyData;
import com.helioroute.calculation.domain.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenTopoDataAdapter implements TopographyProvider {

    private final ExternalTopoLibrary externalLib;
    private final ObjectMapper mapper;

    public OpenTopoDataAdapter() {
        this.externalLib = new ExternalTopoLibrary();
        this.mapper = new ObjectMapper();
    }

    @Override
    public EnvironmentalData getEnvironmentData(Location location) {
        String json = externalLib.fetchCombinedData(location.getLatitude(), location.getLongitude());
        
        try {
            JsonNode root = mapper.readTree(json);
            
            // Extract Current Units
            JsonNode current = root.path("current");
            double temp = current.path("temperature_2m").asDouble();
            double wind = current.path("wind_speed_10m").asDouble();
            int code = current.path("weather_code").asInt();
            double press = current.path("surface_pressure").asDouble(1013.0);
            
            // Elevation is usually in "elevation" root property in Open-Meteo
            double elevation = root.path("elevation").asDouble(500.0);

            // Extract Daily (Sunrise/Sunset)
            JsonNode daily = root.path("daily");
            String sunrise = daily.path("sunrise").get(0).asText();
            String sunset = daily.path("sunset").get(0).asText();

            // Extract Hourly
            JsonNode hourly = root.path("hourly");
            JsonNode times = hourly.path("time");
            JsonNode temps = hourly.path("temperature_2m");
            JsonNode winds = hourly.path("wind_speed_10m");
            JsonNode uvs = hourly.path("uv_index");
            JsonNode dayFlags = hourly.path("is_day");
            
            // New Pro Data from Hourly (Approximate to now or take max/avg)
            // We need to match current hour roughly.
            JsonNode freezing = hourly.path("freezing_level_height");
            JsonNode vis = hourly.path("visibility");
            JsonNode snow = hourly.path("snow_depth");

            List<HourlyData> hourlyList = new ArrayList<>();
            // Get current hour index approx or just current UV
           
            int limit = Math.min(24, times.size());
            double maxUv = 0;
            double currentFreezing = 0;
            double currentVis = 10000;
            double currentSnow = 0;

            for(int i=0; i<limit; i++) {
                HourlyData h = new HourlyData(
                    times.get(i).asText(),
                    temps.get(i).asDouble(),
                    winds.get(i).asDouble(),
                    uvs.get(i).asDouble(),
                    dayFlags.get(i).asInt()
                );
                hourlyList.add(h);
                
                // Simple heuristic for "Current" based on first few hours or average
                if (i == 0) { // Take 'now' as the first element returned (usually starts at requested time or midnight)
                     // Open-Meteo returns hourly from midnight. We should ideally find the matching hour.
                     // For MVP, we'll take the 12th hour (noon) or just the immediate one if we want 'current'.
                     // Actually, 'current' object doesn't have visibility, so let's take index 0 of hourly as "Now-ish".
                     if (freezing.has(i)) currentFreezing = freezing.get(i).asDouble();
                     if (vis.has(i)) currentVis = vis.get(i).asDouble();
                     if (snow.has(i)) currentSnow = snow.get(i).asDouble();
                }
                
                if(h.getUvIndex() > maxUv) maxUv = h.getUvIndex();
            }

            return new EnvironmentalData(
                elevation, temp, wind, code, maxUv, press, currentFreezing, currentVis, currentSnow, sunrise, sunset, hourlyList
            );

        } catch (Exception e) {
            System.err.println("Error parsing Open-Meteo JSON: " + e.getMessage());
            // Fallback
            return new EnvironmentalData(500, 15, 5, 0, 0, 1013, 2000, 10000, 0, "--:--", "--:--", new ArrayList<>());
        }
    }

    private double extractValue(String json, String regex) {
        // Legacy removed
        return 0;
    }
}