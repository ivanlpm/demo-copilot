package com.example.demo.controller;

import com.example.demo.dto.WeatherResponse;
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

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
            new WeatherResponse.Main(20.5, 19.0, 65),
            new WeatherResponse.Weather[]{
                new WeatherResponse.Weather("Clear", "clear sky", "01d")
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
            new WeatherResponse.Main(20.5, 19.0, 65),
            new WeatherResponse.Weather[]{
                new WeatherResponse.Weather("Clear", "clear sky", "01d")
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
}
