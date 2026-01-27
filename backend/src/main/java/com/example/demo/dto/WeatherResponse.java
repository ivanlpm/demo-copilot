package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for weather API response.
 */
public record WeatherResponse(
    @JsonProperty("name") String city,
    Main main,
    Weather[] weather,
    @JsonProperty("cod") int code
) {
    public record Main(
        double temp,
        @JsonProperty("feels_like") double feelsLike,
        double humidity,
        @JsonProperty("temp_min") double tempMin,
        @JsonProperty("temp_max") double tempMax
    ) {}

    public record Weather(
        String main,
        String description,
        String icon
    ) {}
}
