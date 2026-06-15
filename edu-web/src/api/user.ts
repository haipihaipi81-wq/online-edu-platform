import request from './request'

export function getCurrentUser() {
  return request.get('/user/me')
}

export function updateProfile(data: any) {
  return request.put('/user/me', data)
}

export function changePassword(data: any) {
  return request.put('/user/me/password', data)
}

export function getUsers(page: number, size: number, keyword?: string) {
  return request.get('/admin/users', { params: { page, size, keyword } })
}

export function updateUserStatus(id: number, status: number) {
  return request.put(`/admin/users/${id}/status`, { status })
}
