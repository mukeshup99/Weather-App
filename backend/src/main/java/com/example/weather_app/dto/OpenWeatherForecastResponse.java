package com.example.weather_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherForecastResponse {

    private List<Entry> list;
    private City city;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private long dt;
        private Main main;
        private List<Weather> weather;

        @JsonProperty("dt_txt")
        private String dtTxt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;

        @JsonProperty("temp_min")
        private double tempMin;

        @JsonProperty("temp_max")
        private double tempMax;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private String name;
        private String country;
    }
}
