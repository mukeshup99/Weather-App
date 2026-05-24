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

    private String country;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String description;
    private String icon;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    @PrePersist
    protected void onPrePersist() {
        this.searchedAt = LocalDateTime.now();
    }
}