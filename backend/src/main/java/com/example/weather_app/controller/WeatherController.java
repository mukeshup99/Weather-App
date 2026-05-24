package com.example.weather_app.controller;

import com.example.weather_app.dto.WeatherResponse;
import com.example.weather_app.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getWeather(city));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WeatherResponse>> getHistory() {
        return ResponseEntity.ok(weatherService.getHistory());
    }
}