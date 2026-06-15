import request from './request'

export function getCourses(params: any) {
  return request.get('/courses', { params })
}

export function getCourseDetail(id: number) {
  return request.get(`/courses/${id}`)
}

export function createCourse(data: any) {
  return request.post('/courses', data)
}

export function updateCourse(id: number, data: any) {
  return request.put(`/courses/${id}`, data)
}

export function deleteCourse(id: number) {
  return request.delete(`/courses/${id}`)
}

export function getTeacherCourses(page: number, size: number) {
  return request.get('/teacher/courses', { params: { page, size } })
}

export function enrollCourse(id: number) {
  return request.post(`/courses/${id}/enroll`)
}

export function getMyCourses() {
  return request.get('/user/me/courses')
}

export function getCategories() {
  return request.get('/categories')
}

export function getChapters(courseId: number) {
  return request.get(`/courses/${courseId}/chapters`)
}

export function addChapter(courseId: number, data: any) {
  return request.post(`/courses/${courseId}/chapters`, data)
}

export function addSection(courseId: number, chapterId: number, data: any) {
  return request.post(`/courses/${courseId}/chapters/${chapterId}/sections`, data)
}
