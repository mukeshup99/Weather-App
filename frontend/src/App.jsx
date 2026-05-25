import { useState } from 'react'
import { weatherApi } from './api/weatherApi'
import SearchBar from './components/SearchBar'
import CurrentWeatherCard from './components/CurrentWeatherCard'
import ForecastList from './components/ForecastList'
import HistoryList from './components/HistoryList'
import Skeleton from './components/Skeleton'
import UnitsToggle from './components/UnitsToggle'
import LocationButton from './components/LocationButton'
import './App.css'

export default function App() {
  const [current, setCurrent] = useState(null)
  const [forecast, setForecast] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [clearingHistory, setClearingHistory] = useState(false)

  async function search(city) {
    setLoading(true)
    setError('')
    setCurrent(null)
    setForecast(null)
    setHistory([])

    try {
      // Fire all three in parallel; tolerate the optional ones failing.
      const [cur, fc, hist] = await Promise.allSettled([
        weatherApi.current(city),
        weatherApi.forecast(city),
        weatherApi.history(city),
      ])

      if (cur.status === 'fulfilled') setCurrent(cur.value)
      else throw cur.reason

      if (fc.status === 'fulfilled') setForecast(fc.value)
      if (hist.status === 'fulfilled') setHistory(hist.value)
    } catch (e) {
      setError(e.message || 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  async function clearHistory() {
    if (!current?.city) return
    setClearingHistory(true)
    try {
      await weatherApi.clearHistory(current.city)
      setHistory([])
    } catch (e) {
      setError(e.message || 'Could not clear history.')
    } finally {
      setClearingHistory(false)
    }
  }

  async function searchByCoords(lat, lon) {
    setLoading(true)
    setError('')
    setCurrent(null)
    setForecast(null)
    setHistory([])

    try {
      const [cur, fc] = await Promise.allSettled([
        weatherApi.currentByCoords(lat, lon),
        weatherApi.forecastByCoords(lat, lon),
      ])

      if (cur.status === 'fulfilled') setCurrent(cur.value)
      else throw cur.reason

      if (fc.status === 'fulfilled') setForecast(fc.value)

      // Once we know the resolved city name, fetch its history.
      const resolved = cur.value?.city
      if (resolved) {
        try { setHistory(await weatherApi.history(resolved)) } catch (_) {}
      }
    } catch (e) {
      setError(e.message || 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="app-header-row">
          <h1>
            <span aria-hidden="true">⛅</span> Weather App
          </h1>
          <UnitsToggle />
        </div>
        <p className="muted">Powered by Spring Boot + PostgreSQL + OpenWeatherMap</p>
      </header>

      <main className="app-main">
        <SearchBar onSearch={search} loading={loading} />
        <LocationButton
          onCoords={searchByCoords}
          onError={setError}
          loading={loading}
        />

        {error && <div className="error">{error}</div>}

        {!error && !current && !loading && (
          <div className="empty muted">
            Search for a city to see the current weather, a 5-day forecast,
            and your recent lookups.
          </div>
        )}

        {loading && <Skeleton />}

        <CurrentWeatherCard data={current} />
        <ForecastList forecast={forecast} />
        <HistoryList
          history={history}
          onClear={clearHistory}
          clearing={clearingHistory}
        />
      </main>

      <footer className="app-footer muted small">
        <span>Backend expected at <code>http://localhost:8080</code></span>
      </footer>
    </div>
  )
}
