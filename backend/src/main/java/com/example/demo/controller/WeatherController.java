package com.example.demo.controller;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.exception.RateLimitExceededException;
import com.example.demo.exception.WeatherApiException;
import com.example.demo.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<Map<String, String>> handleWeatherApiException(WeatherApiException e) {
        log.error("Weather API error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleRateLimitException(RateLimitExceededException e) {
        log.warn("Rate limit exceeded: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
