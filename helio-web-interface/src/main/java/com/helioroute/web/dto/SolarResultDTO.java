package com.helioroute.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolarResultDTO {
    private boolean sunny;
    private double sunlightPercentage;
    private String calculationMethodUsed;
    
    // Location Metadata
    private String locationName;
    private String locationDescription;
    private String locationImageUrl;
    private String wikiUrl;
    private Double realAltitude; // Added Real Altitude for Known Peaks

    // New Fields
    private double elevation; // Added Elevation
    private double temperature;
    private double windSpeed;
    private double pressure;
    private double visibility;
    private double freezingLevel;
    
    private String thermalSensation;
    private String clothingRecommendation;
    private String safetyAlert;

    // Professional Data
    private double currentUvIndex;
    private String sunrise;
    private String sunset;
    private java.util.List<HourlyDataDTO> hourlyForecast;
}