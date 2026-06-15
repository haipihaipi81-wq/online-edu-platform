import request from './request'

export function getLiveRooms(page: number, size: number) {
  return request.get('/live/rooms', { params: { page, size } })
}

export function getLiveRoom(id: number) {
  return request.get(`/live/rooms/${id}`)
}

export function createLiveRoom(data: any) {
  return request.post('/live/rooms', data)
}

export function startLive(id: number) {
  return request.post(`/live/rooms/${id}/start`)
}

export function endLive(id: number) {
  return request.post(`/live/rooms/${id}/end`)
}

export function getChatHistory(roomId: number, limit: number = 50) {
  return request.get(`/live/rooms/${roomId}/chats`, { params: { limit } })
}

export function getLiveRecords(roomId: number) {
  return request.get(`/live/rooms/${roomId}/records`)
}
