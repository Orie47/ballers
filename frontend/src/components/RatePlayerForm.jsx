import { useState } from 'react'
import { CURRENT_USER_ID } from '../constants'
import { formatSportLabel } from '../utils/format'

const fieldClass =
  'rounded-md border border-(--border) bg-(--bg) px-3 py-2 text-sm text-(--text-h)'

function RatePlayerForm({ games = [], onSubmit, onCancel }) {
  const [gameId, setGameId] = useState('')
  const [playerId, setPlayerId] = useState('')
  const [rating, setRating] = useState(5)
  const [submitting, setSubmitting] = useState(false)

  const selectedGame = games.find((game) => String(game.id) === gameId)

  // A rating is always tied to a specific game and a specific player in it
  // (backend RatingRequest needs gameId + ratedPlayerId). The players you can rate
  // are everyone in the chosen game - the host plus anyone who joined - except
  // yourself, since you can't rate your own play.
  const players = selectedGame
    ? [selectedGame.host, ...(selectedGame.participants ?? [])]
        .filter((player) => player && player.id !== CURRENT_USER_ID)
        .filter(
          (player, index, all) =>
            all.findIndex((other) => other.id === player.id) === index,
        )
    : []

  function handleGameChange(event) {
    setGameId(event.target.value)
    // The previously picked player belongs to the old game - clear it.
    setPlayerId('')
  }

  function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    onSubmit({
      raterId: CURRENT_USER_ID,
      ratedPlayerId: Number(playerId),
      gameId: Number(gameId),
      score: Number(rating),
    }).finally(() => setSubmitting(false))
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3">
      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Game
        <select
          required
          value={gameId}
          onChange={handleGameChange}
          className={fieldClass}
        >
          <option value="" disabled>
            Select a game…
          </option>
          {games.map((game) => (
            <option key={game.id} value={game.id}>
              {formatSportLabel(game.sport)} ·{' '}
              {new Date(game.startTime).toLocaleString([], {
                month: 'short',
                day: 'numeric',
                hour: 'numeric',
                minute: '2-digit',
              })}
            </option>
          ))}
        </select>
      </label>

      <label className="flex flex-col gap-1 text-sm text-(--text)">
        Player
        <select
          required
          value={playerId}
          onChange={(event) => setPlayerId(event.target.value)}
          disabled={!selectedGame || players.length === 0}
          className={fieldClass}
        >
          <option value="" disabled>
            {!selectedGame
              ? 'Pick a game first'
              : players.length === 0
                ? 'No other players in this game'
                : 'Select a player…'}
          </option>
          {players.map((player) => (
            <option key={player.id} value={player.id}>
              {player.username}
            </option>
          ))}
        </select>
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
          disabled={submitting || !gameId || !playerId}
          className="rounded-md bg-(--accent) px-3 py-1.5 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-50"
        >
          {submitting ? 'Submitting…' : 'Submit rating'}
        </button>
      </div>
    </form>
  )
}

export default RatePlayerForm
