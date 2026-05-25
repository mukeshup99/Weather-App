import { useState } from 'react'

/**
 * "Use my location" button. Asks the browser for the user's coordinates
 * via navigator.geolocation, then hands them to the parent via
 * `onCoords(lat, lon)`. Disables itself while a fetch is in flight
 * (the parent passes its own `loading` state) and during the brief
 * window between click and getCurrentPosition resolving, so a double
 * click can't fire two parallel searches.
 */
export default function LocationButton({ onCoords, onError, loading }) {
  const [locating, setLocating] = useState(false)
  const supported = typeof navigator !== 'undefined' && 'geolocation' in navigator

  if (!supported) {
    return null
  }

  function request() {
    setLocating(true)
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setLocating(false)
        onCoords(pos.coords.latitude, pos.coords.longitude)
      },
      (err) => {
        setLocating(false)
        const msg = err.code === 1
          ? 'Location permission denied.'
          : err.code === 3
            ? 'Location request timed out.'
            : 'Unable to read your location.'
        if (onError) onError(msg)
      },
      { enableHighAccuracy: false, timeout: 8000, maximumAge: 60_000 }
    )
  }

  const busy = locating || loading
  return (
    <button
      type="button"
      className="location-btn"
      onClick={request}
      disabled={busy}
      aria-label="Use my current location"
      title="Use my current location"
    >
      {locating ? 'Locating…' : (
        <>
          <span aria-hidden="true" className="location-pin">📍</span> Use my location
        </>
      )}
    </button>
  )
}
