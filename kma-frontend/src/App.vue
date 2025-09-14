<script setup>
import { RouterLink, RouterView } from 'vue-router'
import ConfirmDialog from 'primevue/confirmdialog'
import { isLoading } from './stores/loadingStore'
import { Toast } from 'primevue'
import Navbar from './components/Navbar.vue'
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const currentUser = ref(null)

onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
    } catch { currentUser.value = null }
  }
})

// event handler som tar emot login från LoginView
const onAuthChange = (e) => {
  if (e?.detail) {
    currentUser.value = e.detail
  }
}

window.addEventListener('auth-change', onAuthChange)

// cleanup (valfritt i root men bra vana)
onUnmounted(() => {
  window.removeEventListener('auth-change', onAuthChange)
})
</script>

<template>
  <!-- Visa Navbar endast om vi inte är på login-sidan -->
  <Navbar v-if="route.name !== 'login'" :current-user="currentUser" />
  <RouterView />
  <ConfirmDialog />
  <Toast />
  <div v-if="isLoading" class="overlay-spinner">
    <ProgressSpinner />
  </div>
</template>

<style>
  .overlay-spinner {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }

  @media (max-width: 768px) {
  .p-toast {
    max-width: 90vw !important;
    left: 5vw !important;
    right: 5vw !important;
    top: 1rem !important;
    font-size: 0.85rem !important;
  }

  .p-toast-message {
    padding: 0.75rem 1rem !important;
  }

  .p-toast-detail {
    white-space: normal !important;
    word-break: break-word !important;
  }
}

</style>
