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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/v1/weather")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173"})
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> getCurrent(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        requireCityOrCoords(city, lat, lon);
        WeatherResponse body = (lat != null && lon != null)
                ? weatherService.getCurrentByCoords(lat, lon)
                : weatherService.getCurrent(city);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponse> getForecast(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        requireCityOrCoords(city, lat, lon);
        ForecastResponse body = (lat != null && lon != null)
                ? weatherService.getForecastByCoords(lat, lon)
                : weatherService.getForecast(city);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/history")
    public ResponseEntity<List<WeatherResponse>> getHistory(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getHistory(city));
    }

    /**
     * Either ?city= or ?lat=&lon= must be present. Returns 400 otherwise.
     */
    private static void requireCityOrCoords(String city, Double lat, Double lon) {
        boolean hasCity = city != null && !city.isBlank();
        boolean hasCoords = lat != null && lon != null;
        if (!hasCity && !hasCoords) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Provide either 'city' or both 'lat' and 'lon'");
        }
        if (hasCoords && (lat < -90 || lat > 90 || lon < -180 || lon > 180)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Coordinates out of range: lat must be [-90,90], lon must be [-180,180]");
        }
    }
}
