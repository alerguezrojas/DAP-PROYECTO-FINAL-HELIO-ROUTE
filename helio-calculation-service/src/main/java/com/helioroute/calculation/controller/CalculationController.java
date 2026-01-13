package com.helioroute.calculation.controller;

import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import com.helioroute.calculation.service.SolarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/solar")
public class CalculationController {

    private final SolarService solarService; // Inyectará el Proxy gracias a @Primary

    public CalculationController(SolarService solarService) {
        this.solarService = solarService;
    }

    @GetMapping("/calculate")
    public UnshadowedResult calculate(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time,
            @RequestParam(defaultValue = "fast") String strategy) {

        // Mapeo simple de nombre de estrategia (frontend usa "fast" -> backend bean "fastStrategy")
        String beanName = strategy.equals("precise") ? "preciseStrategy" : "fastStrategy";
        
        return solarService.getSolarInfo(new Location(lat, lon), time, beanName);
    }
}