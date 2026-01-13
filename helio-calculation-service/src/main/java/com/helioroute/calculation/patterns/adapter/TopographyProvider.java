package com.helioroute.calculation.patterns.adapter;

import com.helioroute.calculation.domain.Location;

// TARGET: La interfaz que nuestra aplicación espera usar
import com.helioroute.calculation.domain.EnvironmentalData;

public interface TopographyProvider {
    EnvironmentalData getEnvironmentData(Location location);
}