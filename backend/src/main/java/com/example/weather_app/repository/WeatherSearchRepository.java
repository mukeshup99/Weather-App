package com.example.weather_app.repository;

import com.example.weather_app.model.WeatherSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherSearchRepository extends JpaRepository<WeatherSearch, Long> {

    List<WeatherSearch> findTop10ByCityIgnoreCaseOrderByQueriedAtDesc(String city);

    /** Hard-delete every saved lookup for a city. Returns the number of rows removed. */
    long deleteByCityIgnoreCase(String city);
}
