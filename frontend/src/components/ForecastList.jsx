import { formatTemp, useUnits } from '../units'

function formatDay(iso) {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleDateString(undefined, {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
    })
  } catch (_) {
    return iso
  }
}

export default function ForecastList({ forecast }) {
  const { unit } = useUnits()
  if (!forecast || !forecast.days || forecast.days.length === 0) return null
  return (
    <div className="card">
      <h3>5-day forecast</h3>
      <div className="forecast-grid">
        {forecast.days.map((d, i) => (
          <div className="forecast-day" key={i}>
            <div className="day-label">{formatDay(d.date)}</div>
            {d.icon ? (
              <img
                className="day-icon"
                src={`https://openweathermap.org/img/wn/${d.icon}@2x.png`}
                alt={d.description ?? 'weather icon'}
                width="56"
                height="56"
                loading="lazy"
              />
            ) : null}
            <div className="day-temp">
              {formatTemp(d.maxTempC, unit, { withUnit: false })}
              <span className="muted small">
                {' / '}
                {formatTemp(d.minTempC, unit, { withUnit: false })}
              </span>
            </div>
            <div className="day-desc">{d.description ?? ''}</div>
          </div>
        ))}
      </div>
    </div>
  )
}
