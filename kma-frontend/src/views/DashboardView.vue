<template>
  <div>
    <Dialog
      v-model:visible="showTodo"
      header="Inköpslista"
      :modal="true"
      :style="{ width: '30rem' }"
    >
    <Todo />
    </Dialog>


      <h5>* När man skapar en ny kund så får den inloggande användaren kunden under sig.</h5>
      <Button
        label="Skapa ny kund"
        class="mb-4"
        icon="pi pi-plus"
        @click="createSubscriber"
      />
      <!-- <h5 class="text-xl font-bold mb-2">
        Kunder för {{ selectedUser?.firstName || '– ingen vald (Välj användare i listan ovan)' }}
      </h5> -->
      <SubscriberList
        :subscribers="filteredSubscribers"
        :users="users"
        @user-selected="setSelectedUser"
        @edit-subscriber="editSubscriber"
        @delete-subscriber="deleteSubscriber"
      />
      


      <Dialog
        v-model:visible="showSubForm"
        :header="selectedSub ? 'Redigera följare' : 'Skapa ny följare'"
        :modal="true"
        :closable="false"
        :style="{ width: '40rem' }"
    >

        <form @submit.prevent="submitSubForm" class="p-fluid formgrid grid">
          <div class="field col-12 md:col-4">
            <InputText v-model="subForm.firstName" v-tooltip.focus.top="'Ange förnamn'" placeholder="Förnamn" />
          </div>
          <div class="field col-12 md:col-4">
            <InputText v-model="subForm.lastName" v-tooltip.focus.top="'Ange efternamn'" placeholder="Efternamn" />
          </div>
          <div class="field col-12 md:col-4">
            <InputText v-model="subForm.email" v-tooltip.focus.top="'Ange email'" placeholder="Email" />
          </div>

          <Select
              v-model="subForm.type"
              v-tooltip.focus.top="'Välj vilken typ kunden är'"
              :options="subscriberTypes"
              optionLabel="label"
              optionValue="value"
              placeholder="Välj vilken typ"
            />

          <div v-if="selectedSub" class="field col-12 md:col-6">
            <Select
              v-model="subForm.newOwnerEmail"
              v-tooltip.focus.top="'Välj en ny ägare'"
              :options="users"
              optionLabel="firstName"
              optionValue="email"
              placeholder="Välj ny ägare (valfritt)"
            />
          </div>
          <div class="col-12 flex gap-2 mt-3">
            <Button
              type="submit"
              label="Spara ändringar"
              icon="pi pi-check"
              v-if="selectedSub"
              class="p-button-success"
            />
            <Button
              type="submit"
              label="Skapa"
              icon="pi pi-check"
              v-else
              class="p-button-success"
            />
            <Button
              type="button"
              label="Avbryt"
              icon="pi pi-times"
              class="p-button-secondary"
              @click="cancelSubEdit"
            />
          </div>
        </form>
      </Dialog>
      <div v-if="images.length">
  <h5>Bilder</h5>
  <div class="image-gallery">
      <img
        v-for="img in images"
        :key="img.id"
        :src="`${apiUrl}/api/images/${img.id}`"
        :alt="img.fileName"
        style="max-width: 150px; margin-right: 10px;"
      />
    </div>
  </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import Todo from '@/components/Todo.vue'
import Navbar from '@/components/Navbar.vue'
import SubscriberList from '@/components/SubscriberList.vue'
import { Dialog } from 'primevue'
import { hideLoading, showLoading } from '@/stores/loadingStore'
import { useToaster } from '@/stores/toastStore'
const { showSuccessToast, showErrorToast, showInfoToast } = useToaster()

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const images = ref([]);

const users = ref([])
const subscribers = ref([])

const showTodo = ref(false)

const selectedUser = ref(null)
const currentUser = ref(null)
const showSubForm = ref(false)
const selectedSub = ref(null)
const subscriberTypes = [
  { label: 'Partner', value: 'PARTNER' },
  { label: 'Kund', value: 'KUND' },
]

const subForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  type: '',
  newOwnerEmail: ''
})

const authHeader = localStorage.getItem('auth')
const config = {
  headers: { Authorization: authHeader }
}

const setSelectedUser = (user) => {
  selectedUser.value = user
}

const filteredSubscribers = computed(() => {
  if (!selectedUser.value) return []
  return subscribers.value.filter(s => s.owner?.id === selectedUser.value?.id)
})




























const createSubscriber = () => {
  selectedSub.value = null
  subForm.value = { firstName: '', lastName: '', email: '', type: '' }
  showSubForm.value = true
}

const editSubscriber = (subscriber) => {
  selectedSub.value = subscriber
  subForm.value = {
    firstName: subscriber.firstName,
    lastName: subscriber.lastName,
    email: subscriber.email,
    type: subscriber.type,
    newOwnerEmail: subscriber.owner?.email || ''
  }
  showSubForm.value = true
}

const cancelSubEdit = () => {
  showSubForm.value = false
  selectedSub.value = null
  subForm.value = { firstName: '', lastName: '', email: '', type: '', newOwnerEmail: '' }
}

const submitSubForm = async () => {
  try {
    if (selectedSub.value) {
      // Uppdatera subscriber
      showLoading()
      const res = await axios.put(
        `${apiUrl}/api/subscribers/update/${selectedSub.value.id}`,
        subForm.value,
        config
      )
      const updated = res.data
      const index = subscribers.value.findIndex(s => s.id === updated.id)
      if (index !== -1) subscribers.value[index] = updated
      showSuccessToast('Uppdaterat kund!')
    } else {
      // Skapa ny subscriber (backend sätter ägaren automatiskt via principal)
      showLoading()
      const res = await axios.post(
        `${apiUrl}/api/subscribers/create`,
        subForm.value,
        config
      )
      subscribers.value.push(res.data)
      showSubForm.value = false
      await fetchSubscribers()
      showSuccessToast('Skapat en ny kund!')
    }

    cancelSubEdit()
  } catch (err) {
    showErrorToast('Fel vid skapa/redigera kund')
  } finally {
    hideLoading()
  }
}

const fetchSubscribers = async () => {
  try {
    showLoading()
    const res = await axios.get(`${apiUrl}/api/subscribers/`, config)
    subscribers.value = res.data
  } catch (err) {

  } finally {
    hideLoading()
  }
}



const deleteSubscriber = async (subscriber) => {
  try {
    showLoading()
    await axios.delete(`${apiUrl}/api/subscribers/delete/${subscriber.id}`, config)
    subscribers.value = subscribers.value.filter(s => s.id !== subscriber.id)
    showSuccessToast('Kund borttagen!')
  } catch (err) {
    showErrorToast('Fel vid borttagning av kund!')
  } finally {
    hideLoading()
  }
}

onMounted(async () => {
  const meRes = await axios.get(`${apiUrl}/api/auth/me`, config)
  currentUser.value = meRes.data

  try{
    showInfoToast('Hämtar data..')
    const usersRes = await axios.get(`${apiUrl}/api/users/`, config)
    users.value = usersRes.data

    const subsRes = await axios.get(`${apiUrl}/api/subscribers/`, config)
    subscribers.value = subsRes.data

    showSuccessToast('Data hämtad!')
  } catch (err) {
    showErrorToast('Gick inte att hämta data')
  }
})
</script>
