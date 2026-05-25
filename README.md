# Weather App

Full-stack weather lookup: search a city, see current conditions, a
5-day forecast, and your recent lookups for that city. Built as a
Spring Boot REST API in front of OpenWeatherMap, persisted to
PostgreSQL, with a React + Vite frontend.

## Repo layout

```
.
├── backend/    Spring Boot 3 + Java 21 + JPA/PostgreSQL
└── frontend/   React 18 + Vite 5
```

Each folder has its own README with deeper setup notes.

## Architecture

```
┌────────────┐     /api/v1/weather/*     ┌──────────────┐
│  Frontend  │ ───────────────────────▶  │   Backend    │
│ React+Vite │   (Vite dev proxies to    │ Spring Boot  │
│   :5173    │    http://localhost:8080) │    :8080     │
└────────────┘                           └──────┬───────┘
                                                │
                                ┌───────────────┼──────────────┐
                                ▼                              ▼
                       ┌───────────────┐             ┌────────────────┐
                       │  PostgreSQL   │             │ OpenWeatherMap │
                       │   weatherdb   │             │   REST API     │
                       └───────────────┘             └────────────────┘
```

- The backend calls OpenWeather (`/weather` for current, `/forecast`
  for the 5-day / 3-hour forecast) and saves every "current" lookup
  to the `weather_searches` table.
- The frontend talks only to the backend; in dev, Vite proxies
  `/api/*` to `http://localhost:8080` so CORS doesn't come up.

## API

Base path: `/api/v1/weather`

| Method | Endpoint                | Returns                   |
| ------ | ----------------------- | ------------------------- |
| GET    | `/current?city={city}`  | `WeatherResponse`         |
| GET    | `/forecast?city={city}` | `ForecastResponse`        |
| GET    | `/history?city={city}`  | `List<WeatherResponse>`   |

`WeatherResponse`: `city`, `countryCode`, `tempC`, `feelsLikeC`,
`humidity`, `windSpeed`, `description`, `icon`, `queriedAt`.

`ForecastResponse`: `city`, `countryCode`, `days[]` with
`date`, `minTempC`, `maxTempC`, `description`, `icon`.

Errors:
- `404 City Not Found` — unknown city
- `502 Weather API Error` — upstream OpenWeather failure

## Running locally

You need:
- Java 21+ and Maven
- Node.js 18+ and npm
- A PostgreSQL database called `weatherdb` (user `postgres` / password
  `postgres` by default — see `backend/src/main/resources/application.yaml`)
- An OpenWeatherMap API key

### 1. Start PostgreSQL

Anything works as long as `weatherdb` is reachable on
`localhost:5432`. The quickest path is Docker:

```bash
docker run -d --name weather-pg \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=weatherdb \
  -p 5432:5432 \
  postgres:16
```

The backend will create the `weather_searches` table on first run
(`spring.jpa.hibernate.ddl-auto: update`).

### 2. Start the backend

```bash
cd backend
OPENWEATHER_API_KEY=your_key_here mvn spring-boot:run
```

The API comes up on `http://localhost:8080`.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open <http://localhost:5173>. Search a city — current conditions,
the 5-day forecast, and recent lookups should all render.

## Tech choices

- **Backend**: Spring Boot 3 (web, data-jpa, validation, actuator),
  Lombok, RestTemplate against OpenWeather, PostgreSQL via JDBC.
  Layered into controller / service / repository / dto / model with
  a `@RestControllerAdvice` global exception handler.
- **Frontend**: React 18 with hooks, Vite 5 for dev/build, plain CSS
  (no UI library), `fetch` for API calls. The dev server proxies
  `/api/*` to the backend so the React code uses relative URLs.

## License

Personal project, no license specified.
