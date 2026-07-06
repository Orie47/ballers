import { formatSportLabel } from '../utils/format'

function formatStartTime(startTime) {
  return new Date(startTime).toLocaleTimeString([], {
    hour: 'numeric',
    minute: '2-digit',
  })
}

function GameCard({ game, distanceKm, selected, joined, onSelect, onJoin }) {
  return (
    <li
      onClick={() => onSelect(game.id)}
      className={`cursor-pointer rounded-lg border p-3 transition-colors ${
        selected
          ? 'border-(--accent) bg-(--accent-bg)'
          : 'border-(--border) hover:border-(--accent-border)'
      }`}
    >
      <div className="flex items-center justify-between">
        <span className="font-medium text-(--text-h)">
          {formatSportLabel(game.sport)}
        </span>
        <span className="text-xs text-(--text)">
          {distanceKm.toFixed(1)} km
        </span>
      </div>
      <div className="mt-1 flex items-center justify-between text-sm text-(--text)">
        <span>{game.playersNeeded} players needed</span>
        <span>{formatStartTime(game.startTime)}</span>
      </div>
      <button
        type="button"
        disabled={joined}
        onClick={(event) => {
          event.stopPropagation()
          onJoin(game.id)
        }}
        className="mt-2 w-full rounded-md bg-(--accent) py-1.5 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-50"
      >
        {joined ? 'Joined' : 'Join'}
      </button>
    </li>
  )
}

export default GameCard
