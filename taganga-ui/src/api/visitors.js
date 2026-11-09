import { request } from './client'

export function registerVisitor(payload) {
  return request('/api/visitors', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function loginVisitor(payload) {
  return request('/api/visitors/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
