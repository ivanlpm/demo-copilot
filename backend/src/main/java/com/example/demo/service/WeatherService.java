package com.example.demo.service;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.exception.WeatherApiException;
import com.example.demo.model.WeatherCache;
import com.example.demo.repository.WeatherCacheRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Service to fetch and cache weather data with rate limiting and error fallback.
 */
@Service
@Slf4j
public class WeatherService {

    private final RestClient restClient;
    private final WeatherCacheRepository cacheRepository;
    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final int cacheMinutes;
    private final int maxRequestsPerWindow;
    private final int rateLimitWindowMinutes;

    public WeatherService(
            RestClient.Builder restClientBuilder,
            WeatherCacheRepository cacheRepository,
            RateLimiter rateLimiter,
            ObjectMapper objectMapper,
            @Value("${weather.api.url}") String apiUrl,
            @Value("${weather.api.key:demo}") String apiKey,
            @Value("${weather.cache.minutes:30}") int cacheMinutes,
            @Value("${weather.ratelimit.max:60}") int maxRequestsPerWindow,
            @Value("${weather.ratelimit.window:60}") int rateLimitWindowMinutes
    ) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
        this.cacheRepository = cacheRepository;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.cacheMinutes = cacheMinutes;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.rateLimitWindowMinutes = rateLimitWindowMinutes;
    }

    /**
     * Get weather for a city with caching, rate limiting, and error fallback.
     */
    @Transactional
    public WeatherResponse getWeather(String city) {
        log.info("Fetching weather for city: {}", city);
        
        // Check rate limit
        rateLimiter.checkRateLimit(city.toLowerCase(), maxRequestsPerWindow, rateLimitWindowMinutes);
        
        // Try to get from cache first
        Optional<WeatherCache> cached = cacheRepository.findByCity(city.toLowerCase());
        if (cached.isPresent() && !cached.get().isExpired()) {
            log.info("Returning cached weather for: {}", city);
            return deserializeWeather(cached.get().getWeatherData());
        }
        
        // Fetch from API
        try {
            WeatherResponse response = fetchFromApi(city);
            cacheWeather(city, response);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch weather from API for {}: {}", city, e.getMessage());
            
            // Fallback: return stale cache if available
            if (cached.isPresent()) {
                log.warn("Using stale cache for {} due to API error", city);
                return deserializeWeather(cached.get().getWeatherData());
            }
            
            throw new WeatherApiException("Unable to fetch weather for " + city, e);
        }
    }

    /**
     * Get weather by coordinates (for geolocation).
     */
    @Transactional
    public WeatherResponse getWeatherByCoordinates(double lat, double lon) {
        log.info("Fetching weather for coordinates: lat={}, lon={}", lat, lon);
        
        String cacheKey = String.format("geo_%.4f_%.4f", lat, lon);
        rateLimiter.checkRateLimit(cacheKey, maxRequestsPerWindow, rateLimitWindowMinutes);
        
        Optional<WeatherCache> cached = cacheRepository.findByCity(cacheKey);
        if (cached.isPresent() && !cached.get().isExpired()) {
            log.info("Returning cached weather for coordinates");
            return deserializeWeather(cached.get().getWeatherData());
        }
        
        try {
            WeatherResponse response = fetchFromApiByCoordinates(lat, lon);
            cacheWeather(cacheKey, response);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch weather from API for coordinates: {}", e.getMessage());
            
            if (cached.isPresent()) {
                log.warn("Using stale cache for coordinates due to API error");
                return deserializeWeather(cached.get().getWeatherData());
            }
            
            throw new WeatherApiException("Unable to fetch weather for coordinates", e);
        }
    }

    private WeatherResponse fetchFromApi(String city) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(WeatherResponse.class);
    }

    private WeatherResponse fetchFromApiByCoordinates(double lat, double lon) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(WeatherResponse.class);
    }

    private void cacheWeather(String key, WeatherResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            
            // Delete existing cache if present
            cacheRepository.findByCity(key.toLowerCase())
                    .ifPresent(existing -> cacheRepository.deleteByCity(key.toLowerCase()));
            
            WeatherCache cache = new WeatherCache(key, json, cacheMinutes);
            cacheRepository.save(cache);
            log.info("Cached weather for: {}", key);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize weather response: {}", e.getMessage());
        }
    }

    private WeatherResponse deserializeWeather(String json) {
        try {
            return objectMapper.readValue(json, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            throw new WeatherApiException("Failed to deserialize cached weather data", e);
        }
    }
}
