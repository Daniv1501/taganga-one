import { useMutation } from '@tanstack/react-query'
import { registerVisitor } from '../api/visitors'

export function useRegisterVisitor() {
  return useMutation({ mutationFn: registerVisitor })
}
