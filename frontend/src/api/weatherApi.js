// Thin wrapper around fetch() that talks to the Spring Boot backend.
// In development, requests go to /api/v1/* and Vite proxies them to
// http://localhost:8080 (see vite.config.js).

const BASE = import.meta.env.VITE_API_BASE ?? '/api/v1'

async function request(path, init = {}) {
  const res = await fetch(`${BASE}${path}`, init)
  if (!res.ok) {
    let detail = ''
    try {
      const body = await res.json()
      detail = body.message || body.error || ''
    } catch (_) {
      // body wasn't JSON — fall through
    }
    throw new Error(detail || `Request failed (${res.status})`)
  }
  // 204 No Content -> nothing to parse
  if (res.status === 204) return null
  return res.json()
}

function coordsQuery(lat, lon) {
  return `lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lon)}`
}

export const weatherApi = {
  current: (city) => request(`/weather/current?city=${encodeURIComponent(city)}`),
  forecast: (city) => request(`/weather/forecast?city=${encodeURIComponent(city)}`),
  history: (city) => request(`/weather/history?city=${encodeURIComponent(city)}`),

  currentByCoords: (lat, lon) => request(`/weather/current?${coordsQuery(lat, lon)}`),
  forecastByCoords: (lat, lon) => request(`/weather/forecast?${coordsQuery(lat, lon)}`),

  clearHistory: (city) => request(`/weather/history?city=${encodeURIComponent(city)}`, {
    method: 'DELETE',
  }),
}
