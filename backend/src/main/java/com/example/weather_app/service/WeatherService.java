package com.example.weather_app.service;

import com.example.weather_app.config.OpenWeatherProperties;
import com.example.weather_app.dto.OpenWeatherApiResponse;
import com.example.weather_app.dto.WeatherResponse;
import com.example.weather_app.exception.CityNotFoundException;
import com.example.weather_app.exception.WeatherApiException;
import com.example.weather_app.model.WeatherSearch;
import com.example.weather_app.repository.WeatherSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;
    private final OpenWeatherProperties openWeatherProperties;
    private final WeatherSearchRepository weatherSearchRepository;

    public WeatherResponse getWeather(String city) {
        String url = openWeatherProperties.getBaseUrl()
                + "/weather?q=" + city
                + "&appid=" + openWeatherProperties.getApiKey()
                + "&units=metric";

        OpenWeatherApiResponse apiResponse;
        try {
            apiResponse = restTemplate.getForObject(url, OpenWeatherApiResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException(city);
            }
            throw new WeatherApiException("Error calling weather API: " + ex.getMessage());
        }

        if (apiResponse == null || apiResponse.getName() == null || apiResponse.getName().isBlank()) {
            throw new WeatherApiException("Received empty or invalid response from weather API");
        }

        OpenWeatherApiResponse.Main main = apiResponse.getMain();
        OpenWeatherApiResponse.Sys sys = apiResponse.getSys();
        OpenWeatherApiResponse.Wind wind = apiResponse.getWind();
        OpenWeatherApiResponse.Weather weatherInfo =
                apiResponse.getWeather() != null && !apiResponse.getWeather().isEmpty()
                        ? apiResponse.getWeather().get(0)
                        : null;

        WeatherSearch entity = WeatherSearch.builder()
                .city(apiResponse.getName())
                .country(sys != null ? sys.getCountry() : null)
                .temperature(main != null ? main.getTemp() : 0.0)
                .feelsLike(main != null ? main.getFeelsLike() : 0.0)
                .humidity(main != null ? main.getHumidity() : 0)
                .windSpeed(wind != null ? wind.getSpeed() : 0.0)
                .description(weatherInfo != null ? weatherInfo.getDescription() : null)
                .icon(weatherInfo != null ? weatherInfo.getIcon() : null)
                .build();

        weatherSearchRepository.save(entity);

        return WeatherResponse.builder()
                .city(entity.getCity())
                .country(entity.getCountry())
                .temperature(entity.getTemperature())
                .feelsLike(entity.getFeelsLike())
                .humidity(entity.getHumidity())
                .windSpeed(entity.getWindSpeed())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .fetchedAt(entity.getSearchedAt())
                .build();
    }

    public List<WeatherResponse> getHistory() {
        return weatherSearchRepository.findTop10ByOrderBySearchedAtDesc()
                .stream()
                .map(entity -> WeatherResponse.builder()
                        .city(entity.getCity())
                        .country(entity.getCountry())
                        .temperature(entity.getTemperature())
                        .feelsLike(entity.getFeelsLike())
                        .humidity(entity.getHumidity())
                        .windSpeed(entity.getWindSpeed())
                        .description(entity.getDescription())
                        .icon(entity.getIcon())
                        .fetchedAt(entity.getSearchedAt())
                        .build())
                .collect(Collectors.toList());
    }
}