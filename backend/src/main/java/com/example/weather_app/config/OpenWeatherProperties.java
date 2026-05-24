package com.example.weather_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openweather")
public class OpenWeatherProperties {

    private String apiKey;
    private String baseUrl;
}