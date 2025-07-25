<template>
  <header class="navbar">
    <div class="navbar-user">
      <i class="pi pi-user" style="margin-right: 0.5rem; color: #6c757d;"></i>
      <span v-if="currentUser">
        Inloggad som: <strong>{{ currentUser.firstName }} {{ currentUser.lastName }}</strong>
      </span>
    </div>

    <Button
      label="Logga ut"
      icon="pi pi-sign-out"
      class="p-button-sm p-button-danger"
      @click="logout"
    />
  </header>
</template>

<script setup>
import { defineProps } from 'vue'
import Button from 'primevue/button'

const props = defineProps({
  currentUser: Object
})

const logout = () => {
  localStorage.removeItem('auth')
  location.href = '/'
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  margin-bottom: 50px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 10;
}

.navbar-user {
  display: flex;
  align-items: center;
  font-size: 0.95rem;
  color: #495057;
}
</style>
