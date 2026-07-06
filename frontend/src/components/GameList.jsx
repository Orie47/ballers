import SportFilter from './SportFilter'
import GameCard from './GameCard'
import { MY_LOCATION } from '../constants'
import { haversineDistanceKm } from '../utils/distance'

function GameList({
  games = [],
  loading,
  error,
  sport,
  onSportChange,
  selectedGameId,
  joinedGameIds,
  onSelectGame,
  onJoinGame,
}) {
  return (
    <div className="flex h-full flex-col gap-3">
      <SportFilter value={sport} onChange={onSportChange} />

      {loading && <p className="text-sm text-(--text)">Loading games…</p>}
      {error && <p className="text-sm text-red-500">{error}</p>}
      {!loading && !error && games.length === 0 && (
        <p className="text-sm text-(--text)">No games found nearby.</p>
      )}

      <ul className="flex flex-col gap-2 overflow-y-auto">
        {games.map((game) => (
          <GameCard
            key={game.id}
            game={game}
            distanceKm={haversineDistanceKm(MY_LOCATION, {
              lat: game.lat,
              lng: game.lng,
            })}
            selected={game.id === selectedGameId}
            joined={joinedGameIds.has(game.id)}
            onSelect={onSelectGame}
            onJoin={onJoinGame}
          />
        ))}
      </ul>
    </div>
  )
}

export default GameList
