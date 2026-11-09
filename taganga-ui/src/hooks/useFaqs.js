import { useQuery } from '@tanstack/react-query'
import { getFaqs } from '../api/assistant'

export function useFaqs() {
  return useQuery({ queryKey: ['faqs'], queryFn: getFaqs })
}
