import { useToast } from 'primevue/usetoast'

export function useToaster() {
  const toast = useToast()

  const showSuccessToast = (msg = 'Klart!') => {
    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: msg,
      life: 3000,
    })
  }

  const showErrorToast = (msg = 'Något gick fel') => {
    toast.add({
      severity: 'error',
      summary: 'Fel',
      detail: msg,
      life: 4000,
    })
  }

  return {
    showSuccessToast,
    showErrorToast,
  }
}