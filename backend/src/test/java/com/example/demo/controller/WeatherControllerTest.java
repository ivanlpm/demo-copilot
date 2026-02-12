package com.example.demo.controller;

import com.example.demo.dto.weather.WeatherCacheEntryResponse;
import com.example.demo.dto.weather.WeatherCacheStatisticsResponse;
import com.example.demo.dto.weather.WeatherInfo;
import com.example.demo.dto.weather.WeatherMain;
import com.example.demo.dto.weather.WeatherResponse;
import com.example.demo.exception.RateLimitExceededException;
import com.example.demo.exception.WeatherApiException;
import com.example.demo.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WeatherController.
 */
@SpringBootTest
class WeatherControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private WeatherService weatherService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    @DisplayName("GET /api/weather/{city} should return weather data")
    void getWeatherByCity_Success_ReturnsWeatherData() throws Exception {
        // given
        WeatherResponse response = new WeatherResponse(
            "Malaga",
            new WeatherMain(20.5, 19.0, 65, 18.0, 22.0),
            new WeatherInfo[]{
                new WeatherInfo("Clear", "clear sky", "01d")
            },
            200
        );
        when(weatherService.getWeather("Malaga")).thenReturn(response);

        // when/then
        mockMvc.perform(get("/api/weather/Malaga"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Malaga"))
                .andExpect(jsonPath("$.main.temp").value(20.5))
                .andExpect(jsonPath("$.weather[0].main").value("Clear"));
    }

    @Test
    @DisplayName("GET /api/weather/coordinates should return weather data")
    void getWeatherByCoordinates_Success_ReturnsWeatherData() throws Exception {
        // given
        WeatherResponse response = new WeatherResponse(
            "Malaga",
            new WeatherMain(20.5, 19.0, 65, 18.0, 22.0),
            new WeatherInfo[]{
                new WeatherInfo("Clear", "clear sky", "01d")
            },
            200
        );
        when(weatherService.getWeatherByCoordinates(anyDouble(), anyDouble())).thenReturn(response);

        // when/then
        mockMvc.perform(get("/api/weather/coordinates")
                        .param("lat", "36.7213")
                        .param("lon", "-4.4214"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Malaga"));
    }

    @Test
    @DisplayName("GET /api/weather/{city} should return 503 when API fails")
    void getWeatherByCity_ApiFails_Returns503() throws Exception {
        // given
        when(weatherService.getWeather(anyString()))
            .thenThrow(new WeatherApiException("API unavailable"));

        // when/then
        mockMvc.perform(get("/api/weather/Malaga"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/weather/{city} should return 429 when rate limit exceeded")
    void getWeatherByCity_RateLimitExceeded_Returns429() throws Exception {
        // given
        when(weatherService.getWeather(anyString()))
            .thenThrow(new RateLimitExceededException("Rate limit exceeded"));

        // when/then
        mockMvc.perform(get("/api/weather/Malaga"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/weather/cache/statistics should return cache statistics")
    void getCacheStatistics_ReturnsStatistics() throws Exception {
        // given
        WeatherCacheStatisticsResponse stats = new WeatherCacheStatisticsResponse(10, 8, 2);
        when(weatherService.getCacheStatistics()).thenReturn(stats);

        // when/then
        mockMvc.perform(get("/api/weather/cache/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCached").value(10))
                .andExpect(jsonPath("$.activeCaches").value(8))
                .andExpect(jsonPath("$.expiredCaches").value(2));
    }

    @Test
    @DisplayName("GET /api/weather/cache/list should return cache list")
    void getCacheList_ReturnsCacheList() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<WeatherCacheEntryResponse> cacheList = List.of(
            new WeatherCacheEntryResponse("london", now.minusMinutes(10), now.plusMinutes(20), false),
            new WeatherCacheEntryResponse("paris", now.minusMinutes(5), now.plusMinutes(25), false)
        );
        when(weatherService.getCacheList()).thenReturn(cacheList);

        // when/then
        mockMvc.perform(get("/api/weather/cache/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].city").value("london"))
                .andExpect(jsonPath("$[1].city").value("paris"))
                .andExpect(jsonPath("$[0].expired").value(false));
    }

    @Test
    @DisplayName("DELETE /api/weather/cache/{city} should return 204 when cache invalidated")
    void invalidateCache_WhenCacheExists_Returns204() throws Exception {
        // given
        when(weatherService.invalidateCache("london")).thenReturn(true);

        // when/then
        mockMvc.perform(delete("/api/weather/cache/london"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/weather/cache/{city} should return 404 when cache not found")
    void invalidateCache_WhenCacheNotExists_Returns404() throws Exception {
        // given
        when(weatherService.invalidateCache("unknown")).thenReturn(false);

        // when/then
        mockMvc.perform(delete("/api/weather/cache/unknown"))
                .andExpect(status().isNotFound());
    }
}
