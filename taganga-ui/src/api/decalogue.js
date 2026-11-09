import { request } from './client'

export function getDecalogue() {
  return request('/api/decalogue')
}
