package com.example.demo.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main weather data including temperature and humidity.
 */
public record WeatherMain(
    double temp,
    @JsonProperty("feels_like") double feelsLike,
    double humidity,
    @JsonProperty("temp_min") double tempMin,
    @JsonProperty("temp_max") double tempMax
) {}
