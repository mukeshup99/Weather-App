function formatTime(iso) {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleString()
  } catch (_) {
    return iso
  }
}

export default function CurrentWeatherCard({ data }) {
  if (!data) return null
  return (
    <div className="card current-card">
      <div className="current-header">
        <h2>
          {data.city}
          {data.countryCode ? <span className="muted">, {data.countryCode}</span> : null}
        </h2>
        <span className="muted small">As of {formatTime(data.queriedAt)}</span>
      </div>

      <div className="current-body">
        <div className="temp">
          {data.tempC != null ? `${Math.round(data.tempC)}°C` : '—'}
        </div>
        <div className="meta">
          <div className="desc">{data.description ?? '—'}</div>
          <div className="muted">
            Feels like {data.feelsLikeC != null ? `${Math.round(data.feelsLikeC)}°C` : '—'}
            {' · '}Humidity {data.humidity ?? '—'}%
          </div>
        </div>
      </div>
    </div>
  )
}
