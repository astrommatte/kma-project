import { useToast } from 'primevue/usetoast'

export function useToaster() {
  const toast = useToast()

  const showSuccessToast = (msg) => {
    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: msg,
      life: 3000,
    })
  }

  const showErrorToast = (msg) => {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: msg,
      life: 4000,
    })
  }

  const showInfoToast = (msg) => {
    toast.add({
        severity: 'info',
        summary: 'Info',
        detail: msg,
        life: 4000,
      })
  }

  return {
    showSuccessToast,
    showErrorToast,
    showInfoToast,
  }
}