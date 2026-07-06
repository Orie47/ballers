import { useState } from 'react'

const fieldClass =
  'rounded-md border border-(--border) bg-(--bg) px-3 py-2 text-sm text-(--text-h)'

function RatePlayerForm({ onSubmit, onCancel }) {
  const [userId, setUserId] = useState('')
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [submitting, setSubmitting] = useState(false)

  function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    onSubmit({ userId, rating: Number(rating), comment }).finally(() =>
      setSubmitting(false),
    )
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3">
      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Player user ID
        <input
          type="text"
          required
          value={userId}
          onChange={(event) => setUserId(event.target.value)}
          className={fieldClass}
        />
      </label>

      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Rating
        <select
          value={rating}
          onChange={(event) => setRating(event.target.value)}
          className={fieldClass}
        >
          {[5, 4, 3, 2, 1].map((stars) => (
            <option key={stars} value={stars}>
              {'★'.repeat(stars)}
              {'☆'.repeat(5 - stars)}
            </option>
          ))}
        </select>
      </label>

      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Comment (optional)
        <textarea
          value={comment}
          onChange={(event) => setComment(event.target.value)}
          rows={2}
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
          {submitting ? 'Submitting…' : 'Submit rating'}
        </button>
      </div>
    </form>
  )
}

export default RatePlayerForm
