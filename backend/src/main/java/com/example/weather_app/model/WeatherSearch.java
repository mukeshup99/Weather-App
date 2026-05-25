package com.example.weather_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "weather_searches")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    private String countryCode;
    private double tempC;
    private double feelsLikeC;
    private int humidity;
    private double windSpeed;
    private String description;
    private String icon;

    @Column(nullable = false)
    private LocalDateTime queriedAt;

    @PrePersist
    protected void onPrePersist() {
        this.queriedAt = LocalDateTime.now();
    }
}
