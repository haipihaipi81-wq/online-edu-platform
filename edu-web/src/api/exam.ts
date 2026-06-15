import request from './request'

// Questions
export function getQuestions(params: any) {
  return request.get('/questions', { params })
}

export function addQuestion(data: any) {
  return request.post('/questions', data)
}

// Exams
export function getExams(params: any) {
  return request.get('/exams', { params })
}

export function createExam(data: any) {
  return request.post('/exams', data)
}

export function autoGenerateExam(data: any) {
  return request.post('/exams/auto-generate', data)
}

export function getExamDetail(id: number) {
  return request.get(`/exams/${id}`)
}

export function publishExam(id: number) {
  return request.put(`/exams/${id}/publish`)
}

export function startExam(id: number) {
  return request.post(`/exams/${id}/start`)
}

export function submitExam(recordId: number, answers: any[]) {
  return request.post(`/exams/records/${recordId}/submit`, { answers })
}

export function getExamResult(recordId: number) {
  return request.get(`/exams/records/${recordId}`)
}

export function getMyExamRecords() {
  return request.get('/user/me/exam-records')
}

// Homework
export function getHomeworkList(params: any) {
  return request.get('/homework', { params })
}

export function createHomework(data: any) {
  return request.post('/homework', data)
}

export function submitHomework(id: number, data: any) {
  return request.post(`/homework/${id}/submit`, data)
}

export function getHomeworkSubmits(id: number) {
  return request.get(`/homework/${id}/submits`)
}

export function gradeHomework(submitId: number, data: any) {
  return request.put(`/homework/submits/${submitId}/grade`, data)
}
