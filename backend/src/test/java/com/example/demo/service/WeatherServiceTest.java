package com.example.demo.service;

import com.example.demo.config.WeatherProperties;
import com.example.demo.dto.weather.WeatherInfo;
import com.example.demo.dto.weather.WeatherMain;
import com.example.demo.dto.weather.WeatherResponse;
import com.example.demo.exception.RateLimitExceededException;
import com.example.demo.exception.WeatherApiException;
import com.example.demo.model.WeatherCache;
import com.example.demo.repository.WeatherCacheRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

/**
 * Unit tests for WeatherService.
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherCacheRepository cacheRepository;
    
    @Mock
    private RateLimiter rateLimiter;
    
    private WeatherService weatherService;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private WeatherProperties properties;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        
        properties = new WeatherProperties();
        properties.getApi().setUrl("https://api.weather.test");
        properties.getApi().setKey("test-api-key");
        properties.setCacheMinutes(30);
        properties.getRatelimit().setMax(60);
        properties.getRatelimit().setWindow(60);
        
        weatherService = new WeatherService(
            builder,
            cacheRepository,
            rateLimiter,
            objectMapper,
            properties
        );
    }

    @Test
    @DisplayName("getWeather should fetch from API when cache is empty")
    void getWeather_NoCacheExists_FetchesFromApi() {
        // given
        String city = "Malaga";
        String jsonResponse = """
            {
                "name": "Malaga",
                "cod": 200,
                "main": {"temp": 20.5, "feels_like": 19.0, "humidity": 65, "temp_min": 18.0, "temp_max": 22.0},
                "weather": [{"main": "Clear", "description": "clear sky", "icon": "01d"}]
            }
            """;
        
        when(cacheRepository.findByCity(city.toLowerCase())).thenReturn(Optional.empty());
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("https://api.weather.test/weather")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        
        // when
        WeatherResponse response = weatherService.getWeather(city);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Malaga");
        assertThat(response.main().temp()).isEqualTo(20.5);
        verify(cacheRepository).save(any(WeatherCache.class));
        verify(rateLimiter).checkRateLimit(city.toLowerCase(), 60, 60);
        mockServer.verify();
    }

    @Test
    @DisplayName("getWeather should return mock data when API returns 401")
    void getWeather_ApiReturns401_ReturnsMockData() {
        // given
        String city = "Malaga";
        when(cacheRepository.findByCity(city.toLowerCase())).thenReturn(Optional.empty());
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("https://api.weather.test/weather")))
                .andRespond(withUnauthorizedRequest());
        
        // when
        WeatherResponse response = weatherService.getWeather(city);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Malaga");
        assertThat(response.main().temp()).isEqualTo(22.5); // Mock temp
        assertThat(response.weather()[0].description()).isEqualTo("clear sky");
        mockServer.verify();
    }

    @Test
    @DisplayName("getWeather should return cached data when cache is valid")
    void getWeather_ValidCacheExists_ReturnsCachedData() throws Exception {
        // given
        String city = "Malaga";
        WeatherResponse cachedResponse = new WeatherResponse(
            "Malaga",
            new WeatherMain(20.5, 19.0, 65, 18.0, 22.0),
            new WeatherInfo[]{
                new WeatherInfo("Clear", "clear sky", "01d")
            },
            200
        );
        String cachedJson = objectMapper.writeValueAsString(cachedResponse);
        
        WeatherCache cache = new WeatherCache(city, cachedJson, 30);
        when(cacheRepository.findByCity(city.toLowerCase())).thenReturn(Optional.of(cache));
        
        // when
        WeatherResponse response = weatherService.getWeather(city);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Malaga");
        verify(cacheRepository, never()).save(any());
        verify(rateLimiter).checkRateLimit(city.toLowerCase(), 60, 60);
    }

    @Test
    @DisplayName("getWeather should use stale cache when API fails")
    void getWeather_ApiFails_UsesStaleCache() throws Exception {
        // given
        String city = "Malaga";
        WeatherResponse cachedResponse = new WeatherResponse(
            "Malaga",
            new WeatherMain(20.5, 19.0, 65, 18.0, 22.0),
            new WeatherInfo[]{
                new WeatherInfo("Clear", "clear sky", "01d")
            },
            200
        );
        String cachedJson = objectMapper.writeValueAsString(cachedResponse);
        
        WeatherCache expiredCache = new WeatherCache(city, cachedJson, -10);
        when(cacheRepository.findByCity(city.toLowerCase())).thenReturn(Optional.of(expiredCache));
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("https://api.weather.test/weather")))
                .andRespond(withServerError());
        
        // when
        WeatherResponse response = weatherService.getWeather(city);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Malaga");
    }

    @Test
    @DisplayName("getWeather should throw exception when API fails and no cache exists")
    void getWeather_ApiFailsNoCacheExists_ThrowsException() {
        // given
        String city = "Malaga";
        when(cacheRepository.findByCity(city.toLowerCase())).thenReturn(Optional.empty());
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("https://api.weather.test/weather")))
                .andRespond(withServerError());
        
        // when/then
        assertThatThrownBy(() -> weatherService.getWeather(city))
            .isInstanceOf(WeatherApiException.class)
            .hasMessageContaining("Unable to fetch weather");
    }

    @Test
    @DisplayName("getWeather should throw exception when rate limit exceeded")
    void getWeather_RateLimitExceeded_ThrowsException() {
        // given
        String city = "Malaga";
        doThrow(new RateLimitExceededException("Rate limit exceeded"))
            .when(rateLimiter).checkRateLimit(anyString(), anyInt(), anyInt());
        
        // when/then
        assertThatThrownBy(() -> weatherService.getWeather(city))
            .isInstanceOf(RateLimitExceededException.class)
            .hasMessageContaining("Rate limit exceeded");
    }

    @Test
    @DisplayName("getWeatherByCoordinates should fetch from API successfully")
    void getWeatherByCoordinates_Success_FetchesFromApi() {
        // given
        double lat = 36.7213;
        double lon = -4.4214;
        String jsonResponse = """
            {
                "name": "Malaga",
                "cod": 200,
                "main": {"temp": 20.5, "feels_like": 19.0, "humidity": 65, "temp_min": 18.0, "temp_max": 22.0},
                "weather": [{"main": "Clear", "description": "clear sky", "icon": "01d"}]
            }
            """;
        
        String cacheKey = String.format("geo_%.4f_%.4f", lat, lon);
        when(cacheRepository.findByCity(cacheKey)).thenReturn(Optional.empty());
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("https://api.weather.test/weather")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        
        // when
        WeatherResponse response = weatherService.getWeatherByCoordinates(lat, lon);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Malaga");
        verify(cacheRepository).save(any(WeatherCache.class));
        verify(rateLimiter).checkRateLimit(cacheKey, 60, 60);
        mockServer.verify();
    }

    @Test
    @DisplayName("getWeatherByCoordinates should return cached data when available")
    void getWeatherByCoordinates_CacheExists_ReturnsCachedData() throws Exception {
        // given
        double lat = 36.7213;
        double lon = -4.4214;
        String cacheKey = String.format("geo_%.4f_%.4f", lat, lon);
        
        WeatherResponse cachedResponse = new WeatherResponse(
            "Malaga",
            new WeatherMain(20.5, 19.0, 65, 18.0, 22.0),
            new WeatherInfo[]{
                new WeatherInfo("Clear", "clear sky", "01d")
            },
            200
        );
        String cachedJson = objectMapper.writeValueAsString(cachedResponse);
        
        WeatherCache cache = new WeatherCache(cacheKey, cachedJson, 30);
        when(cacheRepository.findByCity(cacheKey)).thenReturn(Optional.of(cache));
        
        // when
        WeatherResponse response = weatherService.getWeatherByCoordinates(lat, lon);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Malaga");
        verify(cacheRepository, never()).save(any());
    }
}
