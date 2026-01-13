package com.helioroute.calculation.patterns.proxy;

import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import com.helioroute.calculation.service.SolarService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// PROXY
@Service
@Primary // Para que Spring inyecte este por defecto en vez del real
public class CachingSolarProxy implements SolarService {

    private final SolarService realService;
    private final Map<String, UnshadowedResult> cache = new HashMap<>();

    public CachingSolarProxy(@Qualifier("realSolarService") SolarService realService) {
        this.realService = realService;
    }

    @Override
    public UnshadowedResult getSolarInfo(Location location, LocalDateTime time, String strategyName) {
        // Clave de caché simple para demo
        String key = location.getLatitude() + "," + location.getLongitude() + ":" + time.toString() + ":" + strategyName;

        if (cache.containsKey(key)) {
            System.out.println("Returning cached value for: " + key);
            return cache.get(key);
        }

        System.out.println("Cache miss. Delegating to Real Subject...");
        UnshadowedResult result = realService.getSolarInfo(location, time, strategyName);
        cache.put(key, result);
        return result;
    }
}