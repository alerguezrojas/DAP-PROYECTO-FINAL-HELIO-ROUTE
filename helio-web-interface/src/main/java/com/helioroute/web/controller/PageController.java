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
}