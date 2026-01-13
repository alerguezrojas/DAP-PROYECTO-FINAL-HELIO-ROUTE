package com.helioroute.calculation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentalData {
    private double elevation;
    private double temperature; // Celsius
    private double windSpeed;   // km/h
    private int weatherCode;    // WMO code (0=clear, 1-3=cloudy, etc)

    // New Fields
    private double currentUvIndex;
    private double pressure;        // hPa
    private double freezingLevel;   // meters
    private double visibility;      // meters
    private double snowDepth;       // meters
    
    private String sunrise;
    private String sunset;
    private java.util.List<HourlyData> hourlyForecast;
}