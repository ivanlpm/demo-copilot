package com.example.demo.dto.weather;

/**
 * Detailed weather condition info.
 */
public record WeatherInfo(
    String main,
    String description,
    String icon
) {}
