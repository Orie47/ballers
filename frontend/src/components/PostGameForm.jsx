import { useState } from 'react'
import { SPORTS, MY_LOCATION } from '../constants'
import { formatSportLabel } from '../utils/format'

const fieldClass =
  'rounded-md border border-(--border) bg-(--bg) px-3 py-2 text-sm text-(--text-h)'

function PostGameForm({ onSubmit, onCancel }) {
  const [sport, setSport] = useState(SPORTS[0])
  const [playersNeeded, setPlayersNeeded] = useState(4)
  const [startTime, setStartTime] = useState('')
  const [submitting, setSubmitting] = useState(false)

  function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    onSubmit({
      sport,
      playersNeeded: Number(playersNeeded),
      startTime: new Date(startTime).toISOString(),
      lat: MY_LOCATION.lat,
      lng: MY_LOCATION.lng,
    }).finally(() => setSubmitting(false))
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3">
      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Sport
        <select
          value={sport}
          onChange={(event) => setSport(event.target.value)}
          className={fieldClass}
        >
          {SPORTS.map((option) => (
            <option key={option} value={option}>
              {formatSportLabel(option)}
            </option>
          ))}
        </select>
      </label>

      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Players needed
        <input
          type="number"
          min="1"
          required
          value={playersNeeded}
          onChange={(event) => setPlayersNeeded(event.target.value)}
          className={fieldClass}
        />
      </label>

      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Start time
        <input
          type="datetime-local"
          required
          value={startTime}
          onChange={(event) => setStartTime(event.target.value)}
          className={fieldClass}
        />
      </label>

      <div className="mt-2 flex justify-end gap-2">
        <button
          type="button"
          onClick={onCancel}
          className="rounded-md px-3 py-1.5 text-sm text-(--text)"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={submitting}
          className="rounded-md bg-(--accent) px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50"
        >
          {submitting ? 'Posting…' : 'Post game'}
        </button>
      </div>
    </form>
  )
}

export default PostGameForm
