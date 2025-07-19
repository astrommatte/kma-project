<template>
    <div class="p-6">
      <h1 class="text-2xl font-bold mb-4">Adminpanel</h1>
  
      <div v-if="!isAdmin" class="text-red-600">Du har inte behörighet att visa denna sida.</div>
  
      <div v-else>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Lista användare -->
          <div>
            <UsersList
              :users="users"
              @create-user="createUser"
              @edit-user="editUser"
              @delete-user="deleteUser"
            />
          </div>
  
          <!-- Skapa/Redigera användare -->
          <div v-if="showForm">
            <h2 class="text-xl font-semibold mb-2">{{ selectedUser ? 'Redigera användare' : 'Skapa ny användare' }}</h2>
            <form @submit.prevent="submitForm" class="space-y-2 bg-gray-100 p-4 rounded shadow">
              <InputText v-model="form.firstName" v-tooltip.focus.top="'Ange förnamn'" type="text" placeholder="Förnamn" class="w-full border p-2 rounded" />
              <InputText v-model="form.lastName" v-tooltip.focus.top="'Ange efternamn'" type="text" placeholder="Efternamn" class="w-full border p-2 rounded" />
              <InputText v-model="form.email" v-tooltip.focus.top="'Ange email'" type="email" placeholder="Email" class="w-full border p-2 rounded" />
              <InputText v-model="form.password" v-tooltip.focus.top="'Ange lösenord'" type="password" placeholder="Lösenord" class="w-full border p-2 rounded" />
              
              <div class="space-x-2">
                <Button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">
                  {{ selectedUser ? 'Spara ändringar' : 'Skapa' }}
                </Button>
                <Button @click="cancelEdit" class="bg-gray-400 text-white px-4 py-2 rounded" type="button">Avbryt</Button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="mb-3">
      <label>
        <input type="checkbox" v-model="allowRegistration" @change="updateSetting" />
        Tillåt nyregistrering
      </label>
    </div>

  </template>
  
  <script setup>
  import { ref, onMounted, computed, watch } from 'vue'
  import axios from 'axios'
  import UsersList from '@/components/UserList.vue'
  import { InputText } from 'primevue'
  import { showLoading, hideLoading } from '@/stores/loadingStore'
  import { useToaster } from '@/stores/toastStore'
  const { showSuccessToast, showErrorToast } = useToaster()

  const showForm = ref(false)
  const users = ref([])
  const selectedUser = ref(null)
  const allowRegistration = ref(true)
  
  const form = ref({
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  })
  
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

  const authHeader = localStorage.getItem('auth')
  const config = { headers: { Authorization: authHeader } }
  
  const currentUser = ref(null)
  const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')
  
  onMounted(async () => {
    const reg = await axios.get(`${apiUrl}/api/settings/registration`)
    allowRegistration.value = reg.data.allowed

    try{
      showLoading()
      const meRes = await axios.get(`${apiUrl}/api/auth/me`, config)
      currentUser.value = meRes.data
    } catch{
      console.log('')
    } finally {
      hideLoading()
    }

    const res = await axios.get(`${apiUrl}/api/users/`, config)
    users.value = res.data
  })

  const updateSetting = async () => {
    try{
      await axios.put(`${apiUrl}/api/settings/registration`, {
        allowed: allowRegistration.value
      }, config)
      showSuccessToast('Ändrade användarregistrering')
    }catch {
      showErrorToast('Gick inte att ändra användarregistrering ')
    }
  }

  const changePassword = async (userId, newPassword) => {
  try {
    await axios.put(
      `${apiUrl}/api/users/admin/${userId}/password`,
      { newPassword }, // matchar PasswordChangeDTO
      {
        headers: {
          Authorization: localStorage.getItem('auth')
        }
      }
    )
    showSuccessToast('Lösenordet har uppdaterats!')
  } catch (err) {
    showErrorToast('Det gick inte att ändra lösenordet.')
  }
}

  
  const submitForm = async () => {
    try {
      if (selectedUser.value) {
        // Update
        showLoading()
        if (form.value.password && form.value.password.length > 0) {
         await changePassword(selectedUser.value.id, form.value.password)
        }
        const res = await axios.put(`${apiUrl}/api/users/update/${selectedUser.value.id}`, form.value, config)
        const updated = res.data
        const index = users.value.findIndex(u => u.id === updated.id)
        users.value[index] = updated
        showSuccessToast('Användare uppdaterad!')
      } else {
        // Create
        showLoading()
        const res = await axios.post(`${apiUrl}/api/users/create`, form.value, config)
        users.value.push(res.data)
        showForm.value = false
        showSuccessToast('Ny användare skapad!')
      }
      cancelEdit()
    } catch (err) {
      showErrorToast('Fel vid skapa/redigera användare')
    } finally {
      hideLoading()
    }
  }
  
  const deleteUser = async (id) => {
    try {
      showLoading()
      await axios.delete(`${apiUrl}/api/users/delete/${id}`, config)
      users.value = users.value.filter(u => u.id !== id)
      showSuccessToast('Användare borttagen!')
    } catch (err) {
      showErrorToast('Fel vid borttagning av användare')
    } finally{
      hideLoading()
    }
  }

  const createUser = () => {
  selectedUser.value = null
  form.value = {
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  }
  showForm.value = true
}

const cancelCreate = () => {
    selectedUser.value = null
    form.value = {
      firstName: '',
      lastName: '',
      email: '',
      password: ''
    }
    showForm.value = false
  }

  
  const editUser = (user) => {
    selectedUser.value = user
    form.value = {
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      password: ''
    }
    showForm.value = true
  }
  
  const cancelEdit = () => {
    selectedUser.value = null
    form.value = {
      firstName: '',
      lastName: '',
      email: '',
      password: ''
    }
    showForm.value = false
  }
  </script>
  