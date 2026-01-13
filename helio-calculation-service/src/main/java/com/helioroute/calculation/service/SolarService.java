package com.helioroute.calculation.service;

import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import java.time.LocalDateTime;

// SUBJECT Interface para el Proxy
public interface SolarService {
    UnshadowedResult getSolarInfo(Location location, LocalDateTime time, String strategyName);
}