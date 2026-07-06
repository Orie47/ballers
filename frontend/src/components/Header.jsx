function Header({ onPostGame, onRatePlayer }) {
  return (
    <header className="flex items-center justify-between gap-3 border-b border-(--border) px-6 py-4">
      <div className="flex items-baseline gap-3">
        <h1 className="text-2xl font-semibold text-(--text-h)">Ballers</h1>
        <p className="text-sm text-(--text)">Pickup sports, near you.</p>
      </div>
      <div className="flex gap-2">
        <button
          type="button"
          onClick={onRatePlayer}
          className="rounded-md border border-(--border) px-3 py-1.5 text-sm font-medium text-(--text-h)"
        >
          Rate a player
        </button>
        <button
          type="button"
          onClick={onPostGame}
          className="rounded-md bg-(--accent) px-3 py-1.5 text-sm font-medium text-white"
        >
          Post a game
        </button>
      </div>
    </header>
  )
}

export default Header
