// Placeholder cards shown while a search is in flight. Three of them,
// mirroring the real layout (current weather, 5-day forecast, history)
// so the page doesn't visibly jump when the data arrives.
export default function Skeleton() {
  return (
    <>
      <div className="card skeleton-card" aria-hidden="true">
        <div className="skeleton skeleton-line skeleton-title" />
        <div className="skeleton-row">
          <div className="skeleton skeleton-block skeleton-icon" />
          <div className="skeleton skeleton-block skeleton-temp" />
          <div className="skeleton-col">
            <div className="skeleton skeleton-line" />
            <div className="skeleton skeleton-line skeleton-line-short" />
          </div>
        </div>
      </div>

      <div className="card skeleton-card" aria-hidden="true">
        <div className="skeleton skeleton-line skeleton-title" />
        <div className="forecast-grid">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="forecast-day skeleton-day">
              <div className="skeleton skeleton-line skeleton-line-short" />
              <div className="skeleton skeleton-block skeleton-icon-sm" />
              <div className="skeleton skeleton-line" />
            </div>
          ))}
        </div>
      </div>

      <span className="visually-hidden" role="status" aria-live="polite">
        Loading weather data
      </span>
    </>
  )
}
