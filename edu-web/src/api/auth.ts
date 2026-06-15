import request from './request'

export function login(username: string, password: string) {
  return request.post('/auth/login', { username, password })
}

export function register(username: string, password: string, role: string) {
  return request.post('/auth/register', { username, password, role })
}

export function refreshToken(refreshToken: string) {
  return request.post('/auth/refresh', { refreshToken })
}
