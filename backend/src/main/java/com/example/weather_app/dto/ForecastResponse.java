package com.example.weather_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponse {

    private String city;
    private String countryCode;
    private List<Day> days;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Day {
        private LocalDate date;
        private Double minTempC;
        private Double maxTempC;
        private String description;
        private String icon;
    }
}
