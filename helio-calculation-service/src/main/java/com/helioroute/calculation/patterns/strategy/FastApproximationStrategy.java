package com.helioroute.calculation.patterns.strategy;

import com.helioroute.calculation.domain.EnvironmentalData;
import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import com.helioroute.calculation.patterns.adapter.TopographyProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("fastStrategy")
public class FastApproximationStrategy implements ShadowCalculationStrategy {

    @Override
    public UnshadowedResult calculateShadow(Location location, LocalDateTime time, TopographyProvider topoProvider) {
        // Updated "Fast" implementation to use REAL data but faster heuristic logic (e.g. ignoring advanced terrain ray tracing if we had it)
        // Since our "Precise" one currently just uses Open-Meteo, "Fast" will do the same data fetch but maybe simpler interpretation.
        // In a real scenario, "Fast" might check a local cache or a coarse grid. Here, we fix the "Dummy Data" issue.
        
        EnvironmentalData env = topoProvider.getEnvironmentData(location);
        
        int hour = time.getHour();
        boolean isDaytime = hour >= 6 && hour <= 20; // Simplified day check
        
        // Simple logic: If weather code < 3 (Clear/Partly Cloudy) AND it is daytime -> 100% Sun. Else 0 or reduced.
        double sunPercent = (env.getWeatherCode() <= 3 && isDaytime) ? 100.0 : ((isDaytime) ? 30.0 : 0.0);
        
        return new UnshadowedResult(
            isDaytime && env.getWeatherCode() <= 3, 
            sunPercent,
            "Fast Analysis (Basic Weather Data)",
            env.getElevation(),
            env.getTemperature(),
            env.getWindSpeed(),
            env.getPressure(),
            env.getVisibility(),
            env.getFreezingLevel(),
            "Standard Estimate",     // No complex sensation formula
            "Standard Outdoor Gear", // No complex clothing formula
            "Check local warnings",  // No complex risk formula
            env.getCurrentUvIndex(),
            env.getSunrise(),
            env.getSunset(),
            env.getHourlyForecast()
        );
    }
}