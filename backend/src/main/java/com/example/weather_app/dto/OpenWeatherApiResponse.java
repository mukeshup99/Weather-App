package com.example.weather_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherApiResponse {

    private String name;
    private Sys sys;
    private Main main;
    private List<Weather> weather;
    private Wind wind;

    @Data
    public static class Sys {
        private String country;
    }

    @Data
    public static class Main {
        private double temp;

        @JsonProperty("feels_like")
        private double feelsLike;

        private int humidity;
    }

    @Data
    public static class Weather {
        private String description;
        private String icon;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}