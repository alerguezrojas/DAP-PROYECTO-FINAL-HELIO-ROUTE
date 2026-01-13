package com.helioroute.web.controller;

import com.helioroute.web.dto.SolarResultDTO;
import com.helioroute.web.service.BackendClientService;
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

    public PageController(BackendClientService backendService) {
        this.backendService = backendService;
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
        
        model.addAttribute("result", result);
        model.addAttribute("lat", lat);
        model.addAttribute("lon", lon);
        model.addAttribute("time", time);
        
        return "result";
    }
}