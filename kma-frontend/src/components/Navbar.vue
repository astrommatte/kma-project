<template>
  <header class="navbar">
    <!-- Desktop-läge (större än 500px) -->
    <div v-if="!isMobile" class="navbar-desktop">
      <div class="navbar-user">
        <i class="pi pi-user" style="margin-right: 0.5rem; color: #6c757d;"></i>
        <span v-if="currentUser">
          Inloggad som: <strong>{{ currentUser.firstName }} {{ currentUser.lastName }}</strong>
        </span>
      </div>

      <div class="navbar-actions">
        <Button label="Dashboard" icon="pi pi-home" class="p-button-text" @click="router.push('/dashboard')" />
        <Button label="Anteckningar" icon="pi pi-pencil" class="p-button-text" @click="router.push('/notes')" />
        <Button label="Inköpslista" icon="pi pi-list" class="p-button-text" @click="router.push('/todos')" />
        <Button
          v-if="currentUser?.role === 'ADMIN'"
          label="Admin"
          icon="pi pi-cog"
          class="p-button-text"
          @click="router.push('/admin')"
        />
        <Button label="Logga ut" icon="pi pi-sign-out" class="p-button-danger" @click="logout" />
      </div>
    </div>

    <!-- Mobil-läge (under 500px) -->
    <Menubar v-else :model="items">
      <template #start>
        <div class="navbar-user">
          <i class="pi pi-user" style="margin-right: 0.5rem; color: #6c757d;"></i>
          <span v-if="currentUser">
            {{ currentUser.firstName }} {{ currentUser.lastName }}
          </span>
        </div>
      </template>
    </Menubar>
  </header>
</template>

<script setup>
import { defineProps, computed, ref, onMounted, onUnmounted } from 'vue'
import Button from 'primevue/button'
import Menubar from 'primevue/menubar'
import { useRouter } from 'vue-router'

const props = defineProps({
  currentUser: Object
})

const router = useRouter()

const logout = () => {
  localStorage.removeItem('auth')
  router.push('/')
}

// Menubar-items (för mobil)
const items = computed(() => {
  const baseItems = [
    { label: 'Dashboard', icon: 'pi pi-fw pi-home', command: () => router.push('/dashboard') },
    { label: 'Notes', icon: 'pi pi-fw pi-pencil', command: () => router.push('/notes') },
    { label: 'Inköpslista', icon: 'pi pi-fw pi-list', command: () => router.push('/todos') }
  ]

  if (props.currentUser?.role === 'ADMIN') {
    baseItems.push({ label: 'Admin', icon: 'pi pi-fw pi-cog', command: () => router.push('/admin') })
  }

  baseItems.push({
    label: 'Logga ut',
    icon: 'pi pi-fw pi-sign-out',
    command: logout
  })

  return baseItems
})

// Lyssna på window resize → sätt mobil/desktop
const isMobile = ref(window.innerWidth < 500)

const checkScreen = () => {
  isMobile.value = window.innerWidth < 500
}

onMounted(() => window.addEventListener('resize', checkScreen))
onUnmounted(() => window.removeEventListener('resize', checkScreen))
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.navbar-desktop {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.navbar-user {
  display: flex;
  align-items: center;
  font-size: 0.95rem;
  color: #495057;
}

.navbar-actions {
  display: flex;
  gap: 0.5rem;
}
</style>
