import { formatTemp, useUnits } from '../units'

function formatTime(iso) {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleString()
  } catch (_) {
    return iso
  }
}

export default function HistoryList({ history, onClear, clearing }) {
  const { unit } = useUnits()
  if (!history || history.length === 0) return null
  return (
    <div className="card">
      <div className="history-header">
        <h3>Recent lookups</h3>
        {onClear && (
          <button
            type="button"
            className="clear-btn"
            onClick={onClear}
            disabled={clearing}
            aria-label="Clear recent lookups"
          >
            {clearing ? 'Clearing…' : 'Clear'}
          </button>
        )}
      </div>
      <ul className="history-list">
        {history.map((h, i) => (
          <li key={i}>
            <span className="hist-time muted">{formatTime(h.queriedAt)}</span>
            <span className="hist-temp">{formatTemp(h.tempC, unit)}</span>
            <span className="hist-desc">{h.description ?? ''}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}
