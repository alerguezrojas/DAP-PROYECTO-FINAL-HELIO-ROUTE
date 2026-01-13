package com.helioroute.calculation.patterns.strategy;

import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import com.helioroute.calculation.patterns.adapter.TopographyProvider;

import java.time.LocalDateTime;

public interface ShadowCalculationStrategy {
    UnshadowedResult calculateShadow(Location location, LocalDateTime time, TopographyProvider topoProvider);
}