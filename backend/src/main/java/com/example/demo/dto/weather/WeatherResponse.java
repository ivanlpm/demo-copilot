package com.example.demo.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for weather API response.
 */
public record WeatherResponse(
    @JsonProperty("name") String city,
    WeatherMain main,
    WeatherInfo[] weather,
    @JsonProperty("cod") int code
) {}

