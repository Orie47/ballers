import { useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { MY_LOCATION } from '../constants'
import { formatSportLabel } from '../utils/format'

function pinIcon(color, size) {
  return L.divIcon({
    className: '',
    html: `<div style="width:${size}px;height:${size}px;border-radius:50%;background:${color};border:2px solid white;box-shadow:0 1px 4px rgba(0,0,0,0.4)"></div>`,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
  })
}

const myLocationIcon = pinIcon('#3b82f6', 14)

// Keeps every pin (and "my location") visible whenever the game list changes.
function FitBounds({ games = [] }) {
  const map = useMap()

  useEffect(() => {
    const points = [
      [MY_LOCATION.lat, MY_LOCATION.lng],
      ...games.map((game) => [game.lat, game.lng]),
    ]
    if (points.length > 1) {
      map.fitBounds(points, { padding: [40, 40] })
    } else {
      map.setView(points[0], 13)
    }
  }, [games, map])

  return null
}

function GameMap({ games = [], selectedGameId, onSelectGame }) {
  return (
    <MapContainer
      center={[MY_LOCATION.lat, MY_LOCATION.lng]}
      zoom={13}
      className="h-full w-full rounded-lg"
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <FitBounds games={games} />
      <Marker position={[MY_LOCATION.lat, MY_LOCATION.lng]} icon={myLocationIcon}>
        <Popup>My location</Popup>
      </Marker>
      {games.map((game) => (
        <Marker
          key={game.id}
          position={[game.lat, game.lng]}
          icon={pinIcon(
            game.id === selectedGameId ? '#aa3bff' : '#c084fc',
            game.id === selectedGameId ? 22 : 16,
          )}
          eventHandlers={{ click: () => onSelectGame(game.id) }}
        >
          <Popup>{formatSportLabel(game.sport)}</Popup>
        </Marker>
      ))}
    </MapContainer>
  )
}

export default GameMap
