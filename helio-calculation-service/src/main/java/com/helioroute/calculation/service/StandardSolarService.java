package com.helioroute.calculation.service;

import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import com.helioroute.calculation.patterns.adapter.TopographyProvider;
import com.helioroute.calculation.patterns.strategy.ShadowCalculationStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

// REAL SUBJECT
@Service("realSolarService")
public class StandardSolarService implements SolarService {

    private final TopographyProvider topoProvider;
    private final Map<String, ShadowCalculationStrategy> strategies;

    public StandardSolarService(TopographyProvider topoProvider, Map<String, ShadowCalculationStrategy> strategies) {
        this.topoProvider = topoProvider;
        this.strategies = strategies;
    }

    @Override
    public UnshadowedResult getSolarInfo(Location location, LocalDateTime time, String strategyName) {
        ShadowCalculationStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
             throw new IllegalArgumentException("Estrategia no encontrada: " + strategyName);
        }
        return strategy.calculateShadow(location, time, topoProvider);
    }
}