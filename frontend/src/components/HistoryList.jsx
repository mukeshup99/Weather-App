function formatTime(iso) {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleString()
  } catch (_) {
    return iso
  }
}

export default function HistoryList({ history }) {
  if (!history || history.length === 0) return null
  return (
    <div className="card">
      <h3>Recent lookups</h3>
      <ul className="history-list">
        {history.map((h, i) => (
          <li key={i}>
            <span className="hist-time muted">{formatTime(h.queriedAt)}</span>
            <span className="hist-temp">
              {h.tempC != null ? `${Math.round(h.tempC)}°C` : '—'}
            </span>
            <span className="hist-desc">{h.description ?? ''}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}
