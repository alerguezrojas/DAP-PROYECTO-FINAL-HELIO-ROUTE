package com.helioroute.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HourlyDataDTO {
    private String time;
    private double temperature;
    private double windSpeed;
    private double uvIndex;
    private int isDay;
}
