package com.helioroute.calculation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HourlyData {
    private String time;
    private double temperature;
    private double windSpeed;
    private double uvIndex;
    private int isDay; // 1 = Day, 0 = Night
}
