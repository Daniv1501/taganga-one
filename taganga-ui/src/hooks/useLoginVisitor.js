import { useMutation } from '@tanstack/react-query'
import { loginVisitor } from '../api/visitors'

export function useLoginVisitor() {
  return useMutation({ mutationFn: loginVisitor })
}
