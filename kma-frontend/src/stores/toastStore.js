import { useToast } from 'primevue/usetoast'

export function showSuccessToast(message = 'Allt gick bra!') {
  const toast = useToast()
  toast.add({
    severity: 'success',
    summary: 'Klart',
    detail: message,
    life: 3000
  })
}

export function showErrorToast(message = 'Något gick fel.') {
  const toast = useToast()
  toast.add({
    severity: 'error',
    summary: 'Fel',
    detail: message,
    life: 4000
  })
}