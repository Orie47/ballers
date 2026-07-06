import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export function getGames({ sport, lat, lng, radius }) {
  return api
    .get('/games', { params: { sport, lat, lng, radius } })
    .then((response) => response.data.content ?? [])
}

export function joinGame(gameId, userId) {
  return api.post(`/games/${gameId}/join`, { userId })
}

export function createGame(payload) {
  return api.post('/games', payload)
}

export function rateUser(payload) {
  return api.post('/ratings', payload)
}

export function getUser(id) {
  return api.get(`/users/${id}`)
}
