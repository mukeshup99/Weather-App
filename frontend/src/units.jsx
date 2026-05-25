// Small unit-system helper. The backend always sends temperatures in
// Celsius (units=metric on OpenWeather), so conversion happens entirely
// in the UI. The user's choice is persisted to localStorage so it
// survives reloads.

import { createContext, useCallback, useContext, useEffect, useState } from 'react'

const STORAGE_KEY = 'weather-app:units'

export const UNITS = {
  C: { id: 'C', label: '°C', symbol: '°C' },
  F: { id: 'F', label: '°F', symbol: '°F' },
}

export function readStoredUnits() {
  try {
    const stored = window.localStorage.getItem(STORAGE_KEY)
    if (stored === 'F' || stored === 'C') return stored
  } catch (_) {
    // localStorage can throw in private browsing — fall through.
  }
  return 'C'
}

function persist(unit) {
  try {
    window.localStorage.setItem(STORAGE_KEY, unit)
  } catch (_) { /* ignore */ }
}

// Convert a Celsius value to the active unit. Returns null for null /
// undefined input so callers can show a placeholder.
export function convert(tempC, unit) {
  if (tempC == null || Number.isNaN(tempC)) return null
  if (unit === 'F') return tempC * 9 / 5 + 32
  return tempC
}

// Format a Celsius value as a rounded string in the active unit,
// e.g. "21°C" or "70°F". Returns the dash placeholder for null input.
export function formatTemp(tempC, unit, { withUnit = true } = {}) {
  const v = convert(tempC, unit)
  if (v == null) return '—'
  const rounded = Math.round(v)
  if (!withUnit) return `${rounded}°`
  return `${rounded}${UNITS[unit].symbol}`
}

const UnitsContext = createContext({
  unit: 'C',
  setUnit: () => {},
  toggle: () => {},
})

export function UnitsProvider({ children }) {
  const [unit, setUnitState] = useState(() => readStoredUnits())

  useEffect(() => { persist(unit) }, [unit])

  const setUnit = useCallback((next) => {
    if (next === 'C' || next === 'F') setUnitState(next)
  }, [])

  const toggle = useCallback(() => {
    setUnitState((u) => (u === 'C' ? 'F' : 'C'))
  }, [])

  return (
    <UnitsContext.Provider value={{ unit, setUnit, toggle }}>
      {children}
    </UnitsContext.Provider>
  )
}

export function useUnits() {
  return useContext(UnitsContext)
}
