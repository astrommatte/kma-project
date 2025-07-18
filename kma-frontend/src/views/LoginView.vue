<template>
  <ProgressSpinner v-if="loading" style="width: 50px; height: 50px;" strokeWidth="4" />
  <div class="login-container">
    <h2>{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</h2>

    <form @submit.prevent="handleSubmit">
      <input v-model="email" type="email" placeholder="Email" required />
      <input v-model="password" type="password" placeholder="Lösenord" required />

      <!-- Endast vid registrering -->
      <div v-if="isRegistering">
        <input v-model="firstName" type="text" placeholder="Förnamn" required />
        <input v-model="lastName" type="text" placeholder="Efternamn" required />
      </div>

      <Button type="submit">{{ isRegistering ? 'Skapa konto' : 'Logga in' }}</Button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>

    <Button
      v-if="allowRegister"
      class="toggle-button"
      @click="toggleMode"
    >
      {{ isRegistering ? 'Har du redan ett konto? Logga in' : 'Skapa ny användare' }}
    </Button>

  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

import Button from 'primevue/button'

const firstName = ref('')
const lastName = ref('')
const email = ref('')
const password = ref('')
const error = ref(null)
const isRegistering = ref(false)
const router = useRouter()
const allowRegister = ref(true)
const loading = ref(false)

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

onMounted(() => {
  const stored = localStorage.getItem('allowRegister')
  allowRegister.value = stored !== 'false' // default true
})

const toggleMode = () => {
  isRegistering.value = !isRegistering.value
  error.value = null
}

const handleSubmit = async () => {
  error.value = null
  if (isRegistering.value) {
    // Registrering
    try {
      await axios.post(`${apiUrl}/api/users/create`, {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        password: password.value
      })
      // Logga in automatiskt efter registrering
      await login()
    } catch (err) {
      error.value = err.response?.data || 'Fel vid registrering'
    }
  } else {
    await login()
  }
}

const login = async () => {
  try {
    loading.value = true;
    const authHeader = 'Basic ' + btoa(`${email.value}:${password.value}`)
    await axios.get(`${apiUrl}/api/auth/me`, {
      headers: { Authorization: authHeader }
    })

    localStorage.setItem('auth', authHeader)
    router.push('/dashboard')
  } catch (err) {
    error.value = 'Felaktig e-post eller lösenord'
  }
  loading.value = false;
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 2em;
  border: 1px solid #ccc;
  border-radius: 8px;
}
input {
  display: block;
  width: 100%;
  margin-bottom: 1em;
  padding: 0.5em;
}

.error {
  color: red;
  margin-top: 0.5em;
}
</style>
