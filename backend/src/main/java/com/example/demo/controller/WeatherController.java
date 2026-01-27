package com.example.demo.controller;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
