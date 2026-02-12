package com.example.demo.controller;

import com.example.demo.dto.weather.WeatherCacheEntryResponse;
import com.example.demo.dto.weather.WeatherCacheStatisticsResponse;
import com.example.demo.dto.weather.WeatherResponse;
import com.example.demo.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for weather endpoints with error handling.
 */
@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Get weather for a specific city.
     */
    @GetMapping("/{city}")
    public ResponseEntity<WeatherResponse> getWeatherByCity(@PathVariable String city) {
        log.info("Weather request for city: {}", city);
        WeatherResponse response = weatherService.getWeather(city);
        return ResponseEntity.ok(response);
    }

    /**
     * Get weather by coordinates (for geolocation).
     */
    @GetMapping("/coordinates")
    public ResponseEntity<WeatherResponse> getWeatherByCoordinates(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        log.info("Weather request for coordinates: lat={}, lon={}", lat, lon);
        WeatherResponse response = weatherService.getWeatherByCoordinates(lat, lon);
        return ResponseEntity.ok(response);
    }

    /**
     * Get cache statistics.
     */
    @GetMapping("/cache/statistics")
    public ResponseEntity<WeatherCacheStatisticsResponse> getCacheStatistics() {
        log.info("Cache statistics request");
        return ResponseEntity.ok(weatherService.getCacheStatistics());
    }

    /**
     * Get list of all cached entries.
     */
    @GetMapping("/cache/list")
    public ResponseEntity<List<WeatherCacheEntryResponse>> getCacheList() {
        log.info("Cache list request");
        return ResponseEntity.ok(weatherService.getCacheList());
    }

    /**
     * Invalidate cache for a specific city.
     */
    @DeleteMapping("/cache/{city}")
    public ResponseEntity<Void> invalidateCache(@PathVariable String city) {
        log.info("Cache invalidation request for city: {}", city);
        boolean invalidated = weatherService.invalidateCache(city);
        return invalidated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
