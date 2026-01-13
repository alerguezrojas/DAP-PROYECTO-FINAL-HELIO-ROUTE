package com.helioroute.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class LocationInfoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LocationInfoService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getLocationName(double lat, double lon) {
        try {
            // Hardcoded detection for known peaks - Mayor tolerancia (0.05 grados ~5km)
            double tolerance = 0.05;
            
            if (Math.abs(lat - 28.272) < tolerance && Math.abs(lon - (-16.642)) < tolerance) {
                System.out.println("✓ Detected Teide by coordinates");
                return "Teide";
            }
            if (Math.abs(lat - 42.658) < tolerance && Math.abs(lon - 0.387) < tolerance) {
                System.out.println("✓ Detected Monte Perdido by coordinates");
                return "Monte Perdido";
            }
            if (Math.abs(lat - 42.662) < tolerance && Math.abs(lon - 0.661) < tolerance) {
                System.out.println("✓ Detected Aneto by coordinates");
                return "Pico Aneto";
            }
            if (Math.abs(lat - 37.050) < tolerance && Math.abs(lon - (-3.311)) < tolerance) {
                System.out.println("✓ Detected Mulhacén by coordinates");
                return "Mulhacén";
            }
            if (Math.abs(lat - 40.783) < tolerance && Math.abs(lon - (-3.975)) < tolerance) {
                System.out.println("✓ Detected Peñalara by coordinates");
                return "Peñalara";
            }
            if (Math.abs(lat - 27.988) < tolerance && Math.abs(lon - 86.925) < tolerance) {
                System.out.println("✓ Detected Everest by coordinates");
                return "Monte Everest";
            }
            
            String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=16&addressdetails=1", lat, lon);
            
            // Nominatim requires User-Agent
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (compatible; HelioRouteApp/1.0; +http://helioroute.com)");
            headers.set("Accept-Language", "es");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            System.out.println("DEBUG Nominatim Response: " + responseBody);
            
            JsonNode root = objectMapper.readTree(responseBody);
            
            // 1. Try to find local name in address object
            if (root.has("address")) {
                JsonNode address = root.get("address");
                // Priority list for finding a good name - Expandida para más cobertura
                String[] keys = {"peak", "volcano", "mountain", "mountain_pass", "hill", "cliff", "ridge", 
                                 "tourism", "natural", "landform", "landmark", "attraction", 
                                 "place", "locality", "hamlet", "village", "town", "city", 
                                 "suburb", "neighbourhood", "municipality", "county", "state", "region"};
                for (String key : keys) {
                    if (address.has(key)) {
                        String foundName = address.get(key).asText();
                        if (foundName != null && !foundName.trim().isEmpty()) {
                            System.out.println("✓ Found location name via key '" + key + "': " + foundName);
                            return foundName;
                        }
                    }
                }
            }
            
            // 2. Try 'name' field at root level
            if (root.has("name")) {
                String name = root.get("name").asText();
                if (name != null && !name.trim().isEmpty()) {
                    System.out.println("✓ Using direct 'name' field: " + name);
                    return name;
                }
            }
            
            // 3. Fallback to display_name (extraer parte más relevante)
            if (root.has("display_name")) {
                String displayName = root.get("display_name").asText();
                if (displayName != null && !displayName.trim().isEmpty()) {
                    // Intentar extraer lo más relevante (primeras 1-3 partes)
                    String[] parts = displayName.split(",");
                    if (parts.length > 0) {
                        // Si la primera parte es muy corta, incluir la segunda también
                        String simplifiedName = parts[0].trim();
                        if (parts.length > 1 && simplifiedName.length() < 10) {
                            simplifiedName += ", " + parts[1].trim();
                        }
                        System.out.println("✓ Using display_name: " + simplifiedName);
                        return simplifiedName;
                    }
                }
            }
            
            // 4. Último recurso: coordenadas formateadas
            String coordName = String.format("Ubicación %.3f°, %.3f°", lat, lon);
            System.out.println("✓ Using coordinate-based name: " + coordName);
            return coordName;
            
        } catch (Exception e) {
            System.out.println("✗ ERROR in getLocationName: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, devolver coordenadas
            return String.format("Ubicación %.3f°, %.3f°", lat, lon);
        }
    }

    public WikiInfo getWikiInfo(String query) {
        System.out.println("DEBUG: getWikiInfo called for query: [" + query + "]");
        
        // Intentar con el nombre original y variantes
        String[] searchVariants = getSearchVariants(query);
        
        for (String searchQuery : searchVariants) {
            System.out.println("DEBUG: Trying variant: [" + searchQuery + "]");
            WikiInfo result = searchWikipedia(searchQuery);
            if (result != null) {
                return result;
            }
        }
        
        System.out.println("DEBUG: No Wikipedia article found for any variant of: " + query);
        return null;
    }
    
    private String[] getSearchVariants(String query) {
        // Generar variantes del nombre para mejorar la búsqueda
        String normalized = java.text.Normalizer.normalize(query, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        if (query.equalsIgnoreCase("Mulhacén") || normalized.equalsIgnoreCase("Mulhacen")) {
            return new String[]{"Mulhacén", "Pico Mulhacén", "Mulhacen", "Sierra Nevada (España)"};
        } else if (query.equalsIgnoreCase("Peñalara") || normalized.equalsIgnoreCase("Penalara")) {
            // Añadidas variantes muy explícitas para Peñalara
            return new String[]{"Peñalara", "Pico Peñalara", "Pico de Peñalara", "Macizo de Peñalara"};
        } else if (query.equalsIgnoreCase("Pico Aneto") || normalized.equalsIgnoreCase("Pico Aneto") || normalized.equalsIgnoreCase("Aneto")) {
            return new String[]{"Pico Aneto", "Aneto", "Pico de Aneto"};
        } else if (query.equalsIgnoreCase("Monte Perdido")) {
            return new String[]{"Monte Perdido", "Pico Monte Perdido"};
        } else if (query.equalsIgnoreCase("Teide") || query.equalsIgnoreCase("Pico Teide")) {
            return new String[]{"Teide", "Pico del Teide", "Parque nacional del Teide"};
        } else {
            // Estrategia genérica para otros picos
            return new String[]{query, "Pico " + query, "Monte " + query};
        }
    }
    
    private WikiInfo searchWikipedia(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept-Language", "es-ES,es;q=0.9");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // FIX: Usar RestTemplate con variables para que Spring maneje la codificación correctamente.
            // Evitamos codificar manualmente o construir URIs complejos que puedan generar doble codificación.
            String url = "https://es.wikipedia.org/w/api.php?action=query&format=json&prop=extracts|pageimages&exintro=1&explaintext=1&piprop=thumbnail&pithumbsize=400&titles={title}&redirects=1";
            
            System.out.println("DEBUG: Calling Wiki API for title: " + query);
            
            try {
                // Pasamos 'query' como variable para el placeholder {title}
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, query);
                String responseBody = response.getBody();
                System.out.println("DEBUG: Wiki API Response (first 500 chars): " + responseBody.substring(0, Math.min(500, responseBody.length())));
                
                JsonNode root = objectMapper.readTree(responseBody);
                
                if (root.has("query") && root.get("query").has("pages")) {
                    JsonNode pages = root.get("query").get("pages");
                    // Get the first (and usually only) page
                    JsonNode page = pages.fields().next().getValue();
                    
                    if (page.has("missing")) {
                        System.out.println("DEBUG: Wikipedia page not found for: " + query);
                        return null;
                    }
                    
                    WikiInfo info = new WikiInfo();
                    
                    // Extract description
                    if (page.has("extract")) {
                        info.description = page.get("extract").asText();
                    }
                    
                    // Extract image
                    if (page.has("thumbnail") && page.get("thumbnail").has("source")) {
                        info.imageUrl = page.get("thumbnail").get("source").asText();
                    }
                    
                    // Build Wikipedia URL
                    if (page.has("title")) {
                        String title = page.get("title").asText();
                        info.wikiUrl = "https://es.wikipedia.org/wiki/" + java.net.URLEncoder.encode(title.replace(" ", "_"), java.nio.charset.StandardCharsets.UTF_8);
                    }
                    
                    System.out.println("DEBUG: Wiki Info retrieved successfully!");
                    System.out.println("  - Description length: " + (info.description != null ? info.description.length() : 0));
                    System.out.println("  - Image URL: " + info.imageUrl);
                    System.out.println("  - Wiki URL: " + info.wikiUrl);
                    
                    if (info.description != null && !info.description.isEmpty()) {
                        return info;
                    }
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Wiki API error for query '" + query + "': " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("DEBUG: Critical error in searchWikipedia: " + e.getMessage());
        }
        return null;
    }

    public static class WikiInfo {
        public String description;
        public String imageUrl;
        public String wikiUrl;
    }
}
