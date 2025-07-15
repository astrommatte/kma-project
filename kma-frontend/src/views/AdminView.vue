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
              <InputText v-model="form.firstName" type="text" placeholder="Förnamn" class="w-full border p-2 rounded" />
              <InputText v-model="form.lastName" type="text" placeholder="Efternamn" class="w-full border p-2 rounded" />
              <InputText v-model="form.email" type="email" placeholder="Email" class="w-full border p-2 rounded" />
              <InputText v-model="form.password" type="password" placeholder="Lösenord" class="w-full border p-2 rounded" />
              
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
        <input type="checkbox" v-model="allowRegister" />
        Tillåt nyregistrering
      </label>
    </div>

  </template>
  
  <script setup>
  import { ref, onMounted, computed, watch } from 'vue'
  import axios from 'axios'
  import UsersList from '@/components/UserList.vue'
  import { InputText } from 'primevue'

  const showForm = ref(false)
  const users = ref([])
  const selectedUser = ref(null)
  const allowRegister = ref(true)
  
  const form = ref({
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  })
  
  const authHeader = localStorage.getItem('auth')
  const config = { headers: { Authorization: authHeader } }
  
  const currentUser = ref(null)
  const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')
  
  onMounted(async () => {
    const stored = localStorage.getItem('allowRegister')
    if (stored !== null) {
      allowRegister.value = stored === 'true'
    }
    const meRes = await axios.get('http://localhost:8080/api/auth/me', config)
    currentUser.value = meRes.data
  
    const res = await axios.get('http://localhost:8080/api/users/', config)
    users.value = res.data
  })

  watch(allowRegister, (newVal) => {
    localStorage.setItem('allowRegister', newVal)
  })

  const changePassword = async (userId, newPassword) => {
  try {
    await axios.put(
      `http://localhost:8080/api/users/admin/${userId}/password`,
      { newPassword }, // matchar PasswordChangeDTO
      {
        headers: {
          Authorization: localStorage.getItem('auth')
        }
      }
    )
    alert('Lösenordet har uppdaterats!')
  } catch (err) {
    console.error('Fel vid ändring av lösenord:', err)
    alert('Det gick inte att ändra lösenordet.')
  }
}

  
  const submitForm = async () => {
    try {
      if (selectedUser.value) {
        // Update
        if (form.value.password && form.value.password.length > 0) {
         await changePassword(selectedUser.value.id, form.value.password)
        }
        const res = await axios.put(`http://localhost:8080/api/users/update/${selectedUser.value.id}`, form.value, config)
        const updated = res.data
        const index = users.value.findIndex(u => u.id === updated.id)
        users.value[index] = updated
      } else {
        // Create
        const res = await axios.post('http://localhost:8080/api/users/create', form.value, config)
        users.value.push(res.data)
        showForm.value = false
      }
      cancelEdit()
    } catch (err) {
      console.error('Fel vid skapa/redigera användare:', err)
    }
  }
  
  const deleteUser = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/users/delete/${id}`, config)
      users.value = users.value.filter(u => u.id !== id)
    } catch (err) {
      console.error('Fel vid borttagning:', err)
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
  