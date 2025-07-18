<template>
  <div>
    <router-link
      v-if="currentUser?.role === 'ADMIN'"
      to="/admin"
      class="p-button p-button-rounded p-button-purple"
    >
      Adminpanel
    </router-link>

    <Navbar
      :users="users"
      :currentUser="currentUser"
      @user-selected="setSelectedUser"
    />

    
      <h5 class="text-xl font-bold mb-2">
        Kunder för {{ selectedUser?.firstName || '– ingen vald(Välj användare i listan ovan)' }}
      </h5>
      <SubscriberList
        :subscribers="filteredSubscribers"
        @edit-subscriber="editSubscriber"
        @delete-subscriber="deleteSubscriber"
      />
      <h5>* När man skapar en ny kund så får den inloggande användaren kunden under sig.</h5>
      <Button
        label="Skapa ny kund"
        class="mb-4"
        icon="pi pi-plus"
        @click="createSubscriber"
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
            <InputText v-model="subForm.firstName" placeholder="Förnamn" />
          </div>
          <div class="field col-12 md:col-4">
            <InputText v-model="subForm.lastName" placeholder="Efternamn" />
          </div>
          <div class="field col-12 md:col-4">
            <InputText v-model="subForm.email" placeholder="Email" />
          </div>

          <Select
              v-model="subForm.type"
              :options="subscriberTypes"
              optionLabel="label"
              optionValue="value"
              placeholder="Välj vilken typ"
            />

          <div v-if="selectedSub" class="field col-12 md:col-6">
            <Select
              v-model="subForm.newOwnerEmail"
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
    <!-- Noteringsformulär -->
    <Dialog
      v-model:visible="showNoteForm"
      :header="editingNote ? 'Redigera notering' : 'Skapa ny notering'"
      :modal="true"
      :closable="false"
      :style="{ width: '40rem' }"
    >
      <form @submit.prevent="submitNote" class="p-fluid">
        <div class="field mb-3">
          <InputText v-model="noteForm.title" placeholder="Titel" required />
        </div>
        <div class="field mb-3">
          <Textarea v-model="noteForm.content" placeholder="Innehåll" rows="5" required />
        </div>
          <!-- Ny: fil-upload -->
        <div class="field mb-3">
          <input type="file" @change="onFileSelected" />
        </div>

        <div class="flex gap-2 justify-end mt-4">
          <Button type="submit" label="Spara" icon="pi pi-check" class="p-button-success" />
          <Button type="button" label="Avbryt" icon="pi pi-times" class="p-button-secondary" @click="cancelEdit" />
        </div>
      </form>
    </Dialog>

    <!-- Noteringslista -->
    <NotesList
      :notes="notes"
      :current-user="currentUser"
      @create-note="createNote"
      @edit-note="updateNote"
      @delete-note="deleteNote"
    />
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

import Navbar from '@/components/Navbar.vue'
import SubscriberList from '@/components/SubscriberList.vue'
import NotesList from '@/components/NotesList.vue'
import { Dialog } from 'primevue'

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const images = ref([]);

const users = ref([])
const subscribers = ref([])
const notes = ref([])
const editingNote = ref(null)
const showNoteForm = ref(false)
const selectedFile = ref(null);

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

const noteForm = ref({
  title: '',
  content: '',
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

async function fetchImages(noteId) {
  try {
    const res = await axios.get(`${apiUrl}/api/notes/${noteId}/images`, config);
    images.value = res.data;
  } catch (err) {
    console.error('Kunde inte hämta bilder:', err);
  }
}

function onFileSelected(event) {
  selectedFile.value = event.target.files[0];
}


const submitNote = async () => {
  try {
    // 1. Spara eller uppdatera noten som vanligt (exempel från din kod)
    let noteId = null;
    if (editingNote.value) {
      const res = await axios.put(
        `${apiUrl}/api/notes/update/${editingNote.value.id}`,
        noteForm.value,
        config
      );
      noteId = res.data.id;
      // uppdatera notes-lista osv
    } else {
      const res = await axios.post(
        `${apiUrl}/api/notes/create`,
        noteForm.value,
        config
      );
      noteId = res.data.id;
      notes.value.push(res.data);
    }

    // 2. Om fil valts, ladda upp bilden kopplad till noteId
    if (selectedFile.value) {
      const formData = new FormData();
      formData.append('file', selectedFile.value);
      formData.append('noteId', noteId);

      await axios.post(`${apiUrl}/api/images/upload`, formData, {
  headers: {
    'Content-Type': 'multipart/form-data',
    Authorization: localStorage.getItem('auth') || '', // <-- hämta härifrån
  },
});


      selectedFile.value = null;
    }

    // Rensa formulär och stäng dialog
    noteForm.value.title = '';
    noteForm.value.content = '';
    showNoteForm.value = false;
    editingNote.value = null;

    await fetchNotes(); // hämta om noteringar

  } catch (err) {
    console.error('Fel vid spara/redigera:', err);
  }
};

const createNote = () => {
  noteForm.value = { title: '', content: '', images: [] }
  showNoteForm.value = true
}

const cancelCreate = () => {
  editingNote.value = null
  noteForm.value.title = ''
  noteForm.value.content = ''
  showNoteForm.value = false;
}

const updateNote = note => {
  editingNote.value = note
  noteForm.value = {
    title: note.title,
    content: note.content,
    images: [],
  }
  showNoteForm.value = true
}

const cancelEdit = () => {
  editingNote.value = null
  noteForm.value.title = ''
  noteForm.value.content = ''
  noteForm.value.images = []
  showNoteForm.value = false;
}

const deleteNote = async id => {
  try {
    await axios.delete(`${apiUrl}/api/notes/${id}`, config)
    notes.value = notes.value.filter(n => n.id !== id)
  } catch (err) {
    console.error('Fel vid borttagning:', err)
  }
}


























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
      const res = await axios.put(
        `${apiUrl}/api/subscribers/update/${selectedSub.value.id}`,
        subForm.value,
        config
      )
      const updated = res.data
      const index = subscribers.value.findIndex(s => s.id === updated.id)
      if (index !== -1) subscribers.value[index] = updated
      //await fetchSubscribers()
    } else {
      // Skapa ny subscriber (backend sätter ägaren automatiskt via principal)
      const res = await axios.post(
        `${apiUrl}/api/subscribers/create`,
        subForm.value,
        config
      )
      subscribers.value.push(res.data)
      showSubForm.value = false
      await fetchSubscribers()
    }

    cancelSubEdit()
  } catch (err) {
    console.error('Fel vid skapa/redigera subscriber:', err)
  }
}

const fetchSubscribers = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/subscribers/`, config)
    subscribers.value = res.data
  } catch (err) {
    console.error('Kunde inte hämta subscribers:', err)
  }
}

const fetchNotes = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/notes/all`, config)
    notes.value = res.data
  } catch (err) {
    console.error('Kunde inte hämta anteckningar:', err)
  }
}

const deleteSubscriber = async (subscriber) => {
  try {
    await axios.delete(`${apiUrl}/api/subscribers/delete/${subscriber.id}`, config)
    subscribers.value = subscribers.value.filter(s => s.id !== subscriber.id)
  } catch (err) {
    console.error('Kunde inte ta bort subscriber:', err)
  }
}

onMounted(async () => {
  const meRes = await axios.get(`${apiUrl}/api/auth/me`, config)
  currentUser.value = meRes.data

  const usersRes = await axios.get(`${apiUrl}/api/users/`, config)
  users.value = usersRes.data

  const subsRes = await axios.get(`${apiUrl}/api/subscribers/`, config)
  subscribers.value = subsRes.data

  const notesRes = await axios.get(`${apiUrl}/api/notes/all`, config)
  notes.value = notesRes.data
})
</script>
