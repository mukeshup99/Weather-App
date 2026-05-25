import { useState } from 'react'

export default function SearchBar({ onSearch, loading }) {
  const [city, setCity] = useState('')

  function submit(e) {
    e.preventDefault()
    const trimmed = city.trim()
    if (trimmed.length > 0) onSearch(trimmed)
  }

  return (
    <form className="search-bar" onSubmit={submit}>
      <input
        type="text"
        value={city}
        placeholder="Enter a city, e.g. Bengaluru"
        onChange={(e) => setCity(e.target.value)}
        autoFocus
      />
      <button type="submit" disabled={loading || city.trim().length === 0}>
        {loading ? 'Loading…' : 'Search'}
      </button>
    </form>
  )
}
