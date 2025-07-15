<template>
  <div class="logged-in-panel">
    <h5 v-if="currentUser" class="logged-in-color">
      Inloggad som: <strong>{{ currentUser.firstName }} {{ currentUser.lastName }}</strong>
    </h5>

    <Button
      label="Logga ut"
      icon="pi pi-sign-out"
      class="p-button-danger"
      @click="logout"
    />
  </div>
  <div class="p-d-flex p-ai-center p-gap-3 p-bg-gray-800 p-p-3" style="color: white;">
    <Select
      :options="users"
      optionLabel="firstName"
      optionValue="id"
      placeholder="Välj användare"
      class="p-inputtext-sm"
      @change="handleUserSelect"
      :filter="true"
      filterPlaceholder="Sök användare..."
      :showClear="true"
      style="min-width: 200px;"
    />
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import Button from 'primevue/button'
import { Select } from 'primevue'

const props = defineProps({
  users: Array,
  currentUser: Object
})

const emit = defineEmits(['user-selected'])

const handleUserSelect = (e) => {
  // e.value är det valda id:t från Dropdown
  const selected = props.users.find(u => u.id === e.value)
  emit('user-selected', selected)
}

const logout = () => {
  localStorage.removeItem('auth')
  location.href = '/' // eller router.push om du använder Vue Router i setup
}
</script>

<style>
.logged-in-color{
  color: rgba(37, 31, 31, 0.459);
}

.logged-in-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
