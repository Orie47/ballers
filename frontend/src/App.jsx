import { useEffect, useState } from 'react'
import Header from './components/Header'
import GameList from './components/GameList'
import GameMap from './components/GameMap'
import Modal from './components/Modal'
import PostGameForm from './components/PostGameForm'
import RatePlayerForm from './components/RatePlayerForm'
import Toast from './components/Toast'
import { getGames, joinGame, createGame, rateUser } from './api'
import { MY_LOCATION, SEARCH_RADIUS_KM, CURRENT_USER_ID } from './constants'

function App() {
  const [games, setGames] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [sport, setSport] = useState('')
  const [selectedGameId, setSelectedGameId] = useState(null)
  const [joinedGameIds, setJoinedGameIds] = useState(new Set())
  const [reloadToken, setReloadToken] = useState(0)
  const [showPostGameModal, setShowPostGameModal] = useState(false)
  const [showRatePlayerModal, setShowRatePlayerModal] = useState(false)
  const [toastMessage, setToastMessage] = useState(null)

  useEffect(() => {
    let cancelled = false

    setLoading(true)
    setError(null)

    getGames({
      sport: sport || undefined,
      lat: MY_LOCATION.lat,
      lng: MY_LOCATION.lng,
      radius: SEARCH_RADIUS_KM,
    })
      .then((data) => {
        if (cancelled) return
        setGames(data ?? [])
      })
      .catch(() => {
        if (cancelled) return
        setError('Could not load games. Please try again.')
      })
      .finally(() => {
        if (cancelled) return
        setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [sport, reloadToken])

  useEffect(() => {
    if (!toastMessage) return
    const timer = setTimeout(() => setToastMessage(null), 3000)
    return () => clearTimeout(timer)
  }, [toastMessage])

  function refreshGames() {
    setReloadToken((token) => token + 1)
  }

  function handleJoinGame(gameId) {
    joinGame(gameId, CURRENT_USER_ID)
      .then(() => {
        setJoinedGameIds((current) => new Set(current).add(gameId))
      })
      .catch((err) => {
        if (err.response?.status === 409) {
          setToastMessage('This game is full.')
        } else {
          setError('Could not join the game. Please try again.')
        }
      })
  }

  function handleCreateGame(payload) {
    return createGame(payload)
      .then(() => {
        setShowPostGameModal(false)
        refreshGames()
      })
      .catch(() => {
        setError('Could not post the game. Please try again.')
      })
  }

  function handleRatePlayer(payload) {
    return rateUser(payload)
      .then(() => {
        setShowRatePlayerModal(false)
      })
      .catch(() => {
        setError('Could not submit the rating. Please try again.')
      })
  }

  return (
    <div className="flex h-screen flex-col">
      <Header
        onPostGame={() => setShowPostGameModal(true)}
        onRatePlayer={() => setShowRatePlayerModal(true)}
      />
      <main className="grid flex-1 grid-cols-2 gap-4 overflow-hidden p-4">
        <GameList
          games={games}
          loading={loading}
          error={error}
          sport={sport}
          onSportChange={setSport}
          selectedGameId={selectedGameId}
          joinedGameIds={joinedGameIds}
          onSelectGame={setSelectedGameId}
          onJoinGame={handleJoinGame}
        />
        <GameMap
          games={games}
          selectedGameId={selectedGameId}
          onSelectGame={setSelectedGameId}
        />
      </main>

      {showPostGameModal && (
        <Modal title="Post a game" onClose={() => setShowPostGameModal(false)}>
          <PostGameForm
            onSubmit={handleCreateGame}
            onCancel={() => setShowPostGameModal(false)}
          />
        </Modal>
      )}

      {showRatePlayerModal && (
        <Modal title="Rate a player" onClose={() => setShowRatePlayerModal(false)}>
          <RatePlayerForm
            games={games}
            onSubmit={handleRatePlayer}
            onCancel={() => setShowRatePlayerModal(false)}
          />
        </Modal>
      )}

      {toastMessage && (
        <Toast message={toastMessage} onDismiss={() => setToastMessage(null)} />
      )}
    </div>
  )
}

export default App
