# Weather Frontend (React + Vite)

A small React frontend for the Spring Boot weather API. Search a city to see
the current weather, a 5-day forecast, and recent lookups stored in PostgreSQL.

## Requirements

- Node.js 18+ and npm
- The Spring Boot backend running on `http://localhost:8080`

## Quick start

```bash
cd weather-frontend
npm install
npm run dev
```

Open <http://localhost:5173>.

The Vite dev server proxies `/api/*` to `http://localhost:8080` (configured in
`vite.config.js`), so the React app calls relative URLs and you don't need to
configure CORS during development.

## Configuration

Override the backend URL by creating a `.env` file (see `.env.example`):

```
VITE_API_BASE=https://your-backend.example.com/api/v1
```

When pointing at a non-proxied origin, you'll need to enable CORS on the
backend — see the section below.

## Production build

```bash
npm run build      # outputs to dist/
npm run preview    # serves dist/ locally for a smoke test
```

You can serve `dist/` from any static host, or from Spring Boot itself by
copying its contents into `src/main/resources/static/`.

## CORS — Spring Boot configuration

When the React app is **not** behind the Vite proxy (e.g. you're serving
`dist/` from a different origin), enable CORS in Spring Boot. Add this class
to your backend:

```java
package com.example.weather.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
```

## Expected API contract

| Method | Endpoint | Returns |
| --- | --- | --- |
| GET | `/api/v1/weather/current?city={city}`  | `WeatherDto` |
| GET | `/api/v1/weather/forecast?city={city}` | `ForecastDto` with `days: [...]` |
| GET | `/api/v1/weather/history?city={city}`  | `WeatherDto[]` |

`WeatherDto` fields used by the UI: `city`, `countryCode`, `tempC`,
`feelsLikeC`, `humidity`, `description`, `queriedAt`.

`ForecastDto.days[]` fields used by the UI: `date`, `minTempC`, `maxTempC`,
`description`.

If your backend uses different field names, edit `src/api/weatherApi.js` or
adjust the mapping inside each component.

## Project layout

```
weather-frontend/
├── index.html
├── package.json
├── vite.config.js
├── .env.example
└── src/
    ├── main.jsx
    ├── App.jsx
    ├── App.css
    ├── index.css
    ├── api/
    │   └── weatherApi.js
    └── components/
        ├── SearchBar.jsx
        ├── CurrentWeatherCard.jsx
        ├── ForecastList.jsx
        └── HistoryList.jsx
```
