package com.example.weather_app.controller;

import com.example.weather_app.dto.ForecastResponse;
import com.example.weather_app.dto.WeatherResponse;
import com.example.weather_app.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weather")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173"})
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> getCurrent(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getCurrent(city));
    }

    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponse> getForecast(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getForecast(city));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WeatherResponse>> getHistory(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getHistory(city));
    }
}
