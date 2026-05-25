package com.example.weather_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {

    private String city;
    private String countryCode;
    private Double tempC;
    private Double feelsLikeC;
    private Integer humidity;
    private Double windSpeed;
    private String description;
    private String icon;
    private LocalDateTime queriedAt;
}
