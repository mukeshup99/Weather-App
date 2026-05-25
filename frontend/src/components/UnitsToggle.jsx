import { useUnits } from '../units'

export default function UnitsToggle() {
  const { unit, setUnit } = useUnits()
  return (
    <div className="units-toggle" role="group" aria-label="Temperature unit">
      <button
        type="button"
        className={`units-btn ${unit === 'C' ? 'is-active' : ''}`}
        onClick={() => setUnit('C')}
        aria-pressed={unit === 'C'}
      >
        °C
      </button>
      <button
        type="button"
        className={`units-btn ${unit === 'F' ? 'is-active' : ''}`}
        onClick={() => setUnit('F')}
        aria-pressed={unit === 'F'}
      >
        °F
      </button>
    </div>
  )
}
