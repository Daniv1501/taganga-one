import { useQuery } from '@tanstack/react-query'
import { getDecalogue } from '../api/decalogue'

export function useDecalogue() {
  return useQuery({ queryKey: ['decalogue'], queryFn: getDecalogue })
}
