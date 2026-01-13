package com.helioroute.web.controller;

import com.helioroute.web.dto.SolarResultDTO;
import com.helioroute.web.service.BackendClientService;
import com.helioroute.web.service.LocationInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class PageController {

    private final BackendClientService backendService;
    private final LocationInfoService locationService;

    public PageController(BackendClientService backendService, LocationInfoService locationService) {
        this.backendService = backendService;
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/check")
    public String checkSolarInfo(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam String timeStr,
            @RequestParam String strategy,
            Model model) {

        // Parsing manual simple para demo HTML input datetime-local
        LocalDateTime time = LocalDateTime.parse(timeStr);
        
        SolarResultDTO result = backendService.getSolarCalculation(lat, lon, time, strategy);

        // Check for known real altitude
        Double realAlt = getRealAltitude(lat, lon);
        if (realAlt != null) {
            result.setRealAltitude(realAlt);
        }
        
        // Enrich with Location Metadata
        String locationName = locationService.getLocationName(lat, lon);
        System.out.println("=== CONTROLLER DEBUG ===");
        System.out.println("Location Name Retrieved: [" + locationName + "]");
        System.out.println("Coordinates: " + lat + ", " + lon);
        
        // Set the location name (ahora SIEMPRE tenemos un nombre válido del servicio)
        result.setLocationName(locationName);
        
        // SIEMPRE intentar buscar Wiki con el nombre obtenido
        System.out.println("Attempting to fetch Wiki for: [" + locationName + "]");
        LocationInfoService.WikiInfo wiki = locationService.getWikiInfo(locationName);
        if (wiki != null) {
            System.out.println("✓ Wiki retrieved successfully!");
            System.out.println("  - Description length: " + (wiki.description != null ? wiki.description.length() : 0));
            System.out.println("  - Image URL: " + wiki.imageUrl);
            System.out.println("  - Wiki URL: " + wiki.wikiUrl);
            result.setLocationDescription(wiki.description);
            result.setLocationImageUrl(wiki.imageUrl);
            result.setWikiUrl(wiki.wikiUrl);
        } else {
            System.out.println("✗ Wiki returned NULL for location: " + locationName);
        }
        
        model.addAttribute("result", result);
        model.addAttribute("lat", lat);
        model.addAttribute("lon", lon);
        model.addAttribute("time", time);
        
        return "result";
    }

    private Double getRealAltitude(double lat, double lon) {
        if (isClose(lat, 37.050) && isClose(lon, -3.311)) return 3479.0; // Mulhacén
        if (isClose(lat, 42.632) && isClose(lon, 0.658)) return 3404.0; // Aneto
        if (isClose(lat, 28.272) && isClose(lon, -16.642)) return 3715.0; // Teide
        if (isClose(lat, 40.783) && isClose(lon, -3.975)) return 2428.0; // Peñalara
        if (isClose(lat, 27.988) && isClose(lon, 86.925)) return 8849.0; // Everest
        return null;
    }

    private boolean isClose(double a, double b) {
        return Math.abs(a - b) < 0.005;
    }
}