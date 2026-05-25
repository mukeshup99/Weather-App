package com.example.weather_app.service;

import com.example.weather_app.config.OpenWeatherProperties;
import com.example.weather_app.dto.ForecastResponse;
import com.example.weather_app.dto.OpenWeatherApiResponse;
import com.example.weather_app.dto.OpenWeatherForecastResponse;
import com.example.weather_app.dto.WeatherResponse;
import com.example.weather_app.exception.CityNotFoundException;
import com.example.weather_app.exception.WeatherApiException;
import com.example.weather_app.model.WeatherSearch;
import com.example.weather_app.repository.WeatherSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final DateTimeFormatter DT_TXT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final OpenWeatherProperties openWeatherProperties;
    private final WeatherSearchRepository weatherSearchRepository;

    // ----------------------- current weather -----------------------

    public WeatherResponse getCurrent(String city) {
        UriComponentsBuilder url = baseUrl("/weather").queryParam("q", city);
        return executeCurrent(url, city);
    }

    public WeatherResponse getCurrentByCoords(double lat, double lon) {
        UriComponentsBuilder url = baseUrl("/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon);
        return executeCurrent(url, coordLabel(lat, lon));
    }

    private WeatherResponse executeCurrent(UriComponentsBuilder url, String notFoundLabel) {
        OpenWeatherApiResponse apiResponse;
        try {
            apiResponse = restTemplate.getForObject(url.build(false).toUriString(),
                    OpenWeatherApiResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException(notFoundLabel);
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
                .countryCode(sys != null ? sys.getCountry() : null)
                .tempC(main != null ? main.getTemp() : 0.0)
                .feelsLikeC(main != null ? main.getFeelsLike() : 0.0)
                .humidity(main != null ? main.getHumidity() : 0)
                .windSpeed(wind != null ? wind.getSpeed() : 0.0)
                .description(weatherInfo != null ? weatherInfo.getDescription() : null)
                .icon(weatherInfo != null ? weatherInfo.getIcon() : null)
                .build();

        weatherSearchRepository.save(entity);

        return toResponse(entity);
    }

    // ----------------------- forecast -----------------------

    public ForecastResponse getForecast(String city) {
        UriComponentsBuilder url = baseUrl("/forecast").queryParam("q", city);
        return executeForecast(url, city);
    }

    public ForecastResponse getForecastByCoords(double lat, double lon) {
        UriComponentsBuilder url = baseUrl("/forecast")
                .queryParam("lat", lat)
                .queryParam("lon", lon);
        return executeForecast(url, coordLabel(lat, lon));
    }

    private ForecastResponse executeForecast(UriComponentsBuilder url, String notFoundLabel) {
        OpenWeatherForecastResponse apiResponse;
        try {
            apiResponse = restTemplate.getForObject(url.build(false).toUriString(),
                    OpenWeatherForecastResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException(notFoundLabel);
            }
            throw new WeatherApiException("Error calling forecast API: " + ex.getMessage());
        }

        if (apiResponse == null || apiResponse.getList() == null || apiResponse.getList().isEmpty()) {
            throw new WeatherApiException("Received empty forecast from weather API");
        }

        List<ForecastResponse.Day> days = reduceToDailyForecast(apiResponse.getList());

        String name = apiResponse.getCity() != null ? apiResponse.getCity().getName() : notFoundLabel;
        String country = apiResponse.getCity() != null ? apiResponse.getCity().getCountry() : null;

        return ForecastResponse.builder()
                .city(name)
                .countryCode(country)
                .days(days)
                .build();
    }

    // ----------------------- history -----------------------

    public List<WeatherResponse> getHistory(String city) {
        return weatherSearchRepository
                .findTop10ByCityIgnoreCaseOrderByQueriedAtDesc(city)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete every saved lookup for {@code city}. Returns the number of
     * rows removed so the caller can echo it back to the client.
     */
    @Transactional
    public long clearHistory(String city) {
        return weatherSearchRepository.deleteByCityIgnoreCase(city);
    }

    // ----------------------- helpers -----------------------

    private UriComponentsBuilder baseUrl(String path) {
        return UriComponentsBuilder
                .fromUriString(openWeatherProperties.getBaseUrl() + path)
                .queryParam("appid", openWeatherProperties.getApiKey())
                .queryParam("units", "metric");
    }

    private static String coordLabel(double lat, double lon) {
        return String.format("lat=%.4f,lon=%.4f", lat, lon);
    }

    private WeatherResponse toResponse(WeatherSearch entity) {
        return WeatherResponse.builder()
                .city(entity.getCity())
                .countryCode(entity.getCountryCode())
                .tempC(entity.getTempC())
                .feelsLikeC(entity.getFeelsLikeC())
                .humidity(entity.getHumidity())
                .windSpeed(entity.getWindSpeed())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .queriedAt(entity.getQueriedAt())
                .build();
    }

    private List<ForecastResponse.Day> reduceToDailyForecast(List<OpenWeatherForecastResponse.Entry> entries) {
        Map<LocalDate, List<OpenWeatherForecastResponse.Entry>> byDay = new LinkedHashMap<>();
        for (OpenWeatherForecastResponse.Entry entry : entries) {
            LocalDate date = java.time.Instant.ofEpochSecond(entry.getDt())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();
            byDay.computeIfAbsent(date, k -> new ArrayList<>()).add(entry);
        }

        List<ForecastResponse.Day> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<OpenWeatherForecastResponse.Entry>> e : byDay.entrySet()) {
            List<OpenWeatherForecastResponse.Entry> dayEntries = e.getValue();
            double min = dayEntries.stream()
                    .mapToDouble(en -> en.getMain().getTempMin())
                    .min().orElse(0.0);
            double max = dayEntries.stream()
                    .mapToDouble(en -> en.getMain().getTempMax())
                    .max().orElse(0.0);

            // Pick a representative entry — closest to midday UTC — for the icon/description.
            OpenWeatherForecastResponse.Entry rep = dayEntries.stream()
                    .min(Comparator.comparingInt(en -> {
                        int hour = java.time.Instant.ofEpochSecond(en.getDt())
                                .atZone(ZoneOffset.UTC).getHour();
                        return Math.abs(hour - 12);
                    }))
                    .orElse(dayEntries.get(0));

            String desc = null;
            String icon = null;
            if (rep.getWeather() != null && !rep.getWeather().isEmpty()) {
                desc = rep.getWeather().get(0).getDescription();
                icon = rep.getWeather().get(0).getIcon();
            }

            result.add(ForecastResponse.Day.builder()
                    .date(e.getKey())
                    .minTempC(min)
                    .maxTempC(max)
                    .description(desc)
                    .icon(icon)
                    .build());

            if (result.size() == 5) break;
        }
        return result;
    }
}
