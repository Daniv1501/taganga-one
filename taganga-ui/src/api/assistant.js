import { request } from './client'

export function getFaqs() {
  return request('/api/assistant/faqs')
}

export function askAssistant({ question, visitorName }) {
  return request('/api/assistant/questions', {
    method: 'POST',
    body: JSON.stringify({ question, visitorName }),
  })
}
