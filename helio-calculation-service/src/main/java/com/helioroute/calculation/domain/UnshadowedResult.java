package com.helioroute.calculation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnshadowedResult {
    private boolean isSunny;
    private double sunlightPercentage;
    private String calculationMethodUsed;
    
    // New Fields for Enhanced Report
    private double elevation; // Added Elevation
    private double temperature;
    private double windSpeed;
    private double pressure;
    private double visibility;
    private double freezingLevel;
    
    private String thermalSensation; // "Freezing", "Cold", "Comfortable", "Hot"
    private String clothingRecommendation;
    private String safetyAlert; // "Low Risk", "Hypothermia Risk", "Heatstroke Risk"

    // New Professional Data
    private double currentUvIndex;
    private String sunrise;
    private String sunset;
    private java.util.List<HourlyData> hourlyForecast;
}