<script setup>
import { RouterLink, RouterView } from 'vue-router'
import ConfirmDialog from 'primevue/confirmdialog'
import { isLoading } from './stores/loadingStore'
import { Toast } from 'primevue'
</script>

<template>
  <RouterView />
  <ConfirmDialog />
  <Toast />
  <div v-if="isLoading" class="overlay-spinner">
    <ProgressSpinner />
  </div>
</template>

<style scoped>
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
</style>
