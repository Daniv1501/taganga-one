import { request } from './client'

export function getZones() {
  return request('/api/zones')
}
