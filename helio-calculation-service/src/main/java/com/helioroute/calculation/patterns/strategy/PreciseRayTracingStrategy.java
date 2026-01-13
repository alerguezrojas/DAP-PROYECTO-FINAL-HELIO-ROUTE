package com.helioroute.calculation.patterns.strategy;

import com.helioroute.calculation.domain.EnvironmentalData;
import com.helioroute.calculation.domain.Location;
import com.helioroute.calculation.domain.UnshadowedResult;
import com.helioroute.calculation.patterns.adapter.TopographyProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("preciseStrategy")
public class PreciseRayTracingStrategy implements ShadowCalculationStrategy {

    @Override
    public UnshadowedResult calculateShadow(Location location, LocalDateTime time, TopographyProvider topoProvider) {
        
        // 1. Obtener datos reales de la API (Adapter)
        EnvironmentalData env = topoProvider.getEnvironmentData(location);
        
        // 2. Calcular posición solar aproximada (Azimuth/Elevación)
        // Simplificado: Altura solar depende de hora
        int hour = time.getHour();
        double sunAngle = 0;
        if (hour >= 6 && hour <= 20) {
            sunAngle = (hour - 6) * 12.0; // 0 a 180 grados
            if (sunAngle > 90) sunAngle = 180 - sunAngle; // Simetria mediodia
        }

        // 3. Simular obstrucción (Si elevación terreno es baja pero hay "montaña virtual")
        // En un proyecto real cruzariamos con modelo digital de elevación (DEM).
        // Aquí simulamos: Si ángulo solar < 15 grados y estamos bajos, se tapa.
        boolean isSunny = (sunAngle > 15) && (env.getWeatherCode() < 3); // <3 es poco nuboso

        // 4. Lógica de Riesgo (Risk Logic) - Lo que añade valor real
        // Wind Chill formula approx: T - (v^0.7)
        double windChill = env.getTemperature() - Math.pow(env.getWindSpeed(), 0.7);
        
        String sensation;
        String clothing;
        String risk;

        if (windChill < 0) {
            sensation = "Helada Extrema";
            clothing = "Ropa Térmica Pesada, Cortavientos, Guantes";
            risk = "ALTO RIESGO DE HIPOTERMIA";
        } else if (windChill < 10) {
            sensation = "Frío Intenso";
            clothing = "Capas, Forro Polar, Gorro";
            risk = "Riesgo Moderado de Frío";
        } else if (windChill > 30) {
            sensation = "Calor Excesivo";
            clothing = "Ropa ligera, transpirable, sombrero";
            risk = isSunny ? "ALTO RIESGO DE INSOLACIÓN" : "Calor sofocante";
        } else {
            sensation = "Confortable";
            clothing = "Ropa estandar de montaña";
            risk = "Condiciones Seguras";
        }

        // Ajustar si está nublado o en sombra
        if (!isSunny && windChill < 15) {
            sensation += " (Sombra)";
            risk += " - Sensación térmica reducida por falta de sol";
        }
        
        // UV Logic
        if (env.getCurrentUvIndex() > 6) {
            risk += " | ALERTA UV EXTREMA";
            clothing += " | Gafas de sol y crema sol. obigatorias";
        }
        
        // MOUNTAIN SPECIFIC ALERTS
        if (env.getSnowDepth() > 0.3) {
            risk += " | RIESGO DE ALUD (Nieve Profunda)";
            clothing += " | Raquetas/Crampones REQUERIDOS";
        }
        if (env.getElevation() > env.getFreezingLevel()) {
            sensation += " (Bajo Cero)";
            risk += " | TERRENO CONGELADO";
        }
        if (env.getVisibility() < 1000) {
            risk += " | VISIBILIDAD NULA";
        }

        return new UnshadowedResult(
            isSunny,
            isSunny ? (100 - (env.getWeatherCode() * 10)) : 10.0,
            "Precise Analysis (Real Meteo Data + UV/Timeline)",
            env.getElevation(),
            env.getTemperature(),
            env.getWindSpeed(),
            env.getPressure(),
            env.getVisibility(),
            env.getFreezingLevel(),
            sensation,
            clothing,
            risk,
            env.getCurrentUvIndex(),
            env.getSunrise(),
            env.getSunset(),
            env.getHourlyForecast()
        );
    }
}