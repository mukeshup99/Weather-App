import { formatTemp, useUnits } from '../units'

function formatTime(iso) {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleString()
  } catch (_) {
    return iso
  }
}

export default function CurrentWeatherCard({ data }) {
  const { unit } = useUnits()
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
        {data.icon ? (
          <img
            className="current-icon"
            src={`https://openweathermap.org/img/wn/${data.icon}@2x.png`}
            alt={data.description ?? 'weather icon'}
            width="96"
            height="96"
          />
        ) : null}
        <div className="temp">{formatTemp(data.tempC, unit)}</div>
        <div className="meta">
          <div className="desc">{data.description ?? '—'}</div>
          <div className="muted">
            Feels like {formatTemp(data.feelsLikeC, unit)}
            {' · '}Humidity {data.humidity ?? '—'}%
          </div>
        </div>
      </div>
    </div>
  )
}
