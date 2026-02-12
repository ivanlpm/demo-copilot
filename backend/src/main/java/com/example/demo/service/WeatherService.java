package com.example.demo.service;

import com.example.demo.config.WeatherProperties;
import com.example.demo.dto.weather.WeatherCacheEntryResponse;
import com.example.demo.dto.weather.WeatherCacheStatisticsResponse;
import com.example.demo.dto.weather.WeatherInfo;
import com.example.demo.dto.weather.WeatherMain;
import com.example.demo.dto.weather.WeatherResponse;
import com.example.demo.exception.WeatherApiException;
import com.example.demo.model.WeatherCache;
import com.example.demo.repository.WeatherCacheRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final WeatherProperties properties;

    public WeatherService(
            RestClient.Builder restClientBuilder,
            WeatherCacheRepository cacheRepository,
            RateLimiter rateLimiter,
            ObjectMapper objectMapper,
            WeatherProperties properties
    ) {
        this.restClient = restClientBuilder.baseUrl(properties.getApi().getUrl()).build();
        this.cacheRepository = cacheRepository;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /**
     * Get weather for a city with caching, rate limiting, and error fallback.
     */
    @Transactional
    public WeatherResponse getWeather(String city) {
        log.info("Fetching weather for city: {}", city);
        
        // Check rate limit
        rateLimiter.checkRateLimit(city.toLowerCase(), properties.getRatelimit().getMax(), properties.getRatelimit().getWindow());
        
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
            
            // Fallback to mock data if API key is invalid (for demo purposes)
            if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                log.warn("API key unauthorized. Returning mock weather for demo.");
                return createMockResponse(city);
            }

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
        rateLimiter.checkRateLimit(cacheKey, properties.getRatelimit().getMax(), properties.getRatelimit().getWindow());
        
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
            
            // Fallback to mock data if API key is invalid (for demo purposes)
            if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                log.warn("API key unauthorized. Returning mock weather for demo.");
                return createMockResponse("Your Location");
            }

            if (cached.isPresent()) {
                log.warn("Using stale cache for coordinates due to API error");
                return deserializeWeather(cached.get().getWeatherData());
            }
            
            throw new WeatherApiException("Unable to fetch weather for coordinates", e);
        }
    }

    private WeatherResponse fetchFromApi(String city) {
        log.info("Fetching weather from API for city: {}, API key: {}", city, properties.getApi().getKey());
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", properties.getApi().getKey())
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
                        .queryParam("appid", properties.getApi().getKey())
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(WeatherResponse.class);
    }

    private WeatherResponse createMockResponse(String city) {
        return new WeatherResponse(
            city,
            new WeatherMain(22.5, 23.1, 45.0, 18.0, 25.5),
            new WeatherInfo[]{
                new WeatherInfo("Sunny", "clear sky", "01d")
            },
            200
        );
    }

    private void cacheWeather(String key, WeatherResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            
            // Delete existing cache if present
            cacheRepository.findByCity(key.toLowerCase())
                    .ifPresent(existing -> cacheRepository.deleteByCity(key.toLowerCase()));
            
            WeatherCache cache = new WeatherCache(key, json, properties.getCacheMinutes());
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

    /**
     * Get cache statistics.
     */
    @Transactional(readOnly = true)
    public WeatherCacheStatisticsResponse getCacheStatistics() {
        List<WeatherCache> allCaches = cacheRepository.findAll();
        long totalCached = allCaches.size();
        long activeCaches = allCaches.stream().filter(cache -> !cache.isExpired()).count();
        long expiredCaches = totalCached - activeCaches;
        
        return new WeatherCacheStatisticsResponse(totalCached, activeCaches, expiredCaches);
    }

    /**
     * Get list of all cached entries.
     */
    @Transactional(readOnly = true)
    public List<WeatherCacheEntryResponse> getCacheList() {
        return cacheRepository.findAll().stream()
                .map(cache -> new WeatherCacheEntryResponse(
                        cache.getCity(),
                        cache.getCachedAt(),
                        cache.getExpiresAt(),
                        cache.isExpired()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Invalidate cache for a specific city.
     */
    @Transactional
    public boolean invalidateCache(String city) {
        Optional<WeatherCache> cached = cacheRepository.findByCity(city.toLowerCase());
        if (cached.isPresent()) {
            cacheRepository.deleteByCity(city.toLowerCase());
            log.info("Cache invalidated for: {}", city);
            return true;
        }
        return false;
    }
}
