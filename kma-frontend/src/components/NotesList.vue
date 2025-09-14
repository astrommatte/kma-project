<template>
    <div class="mb-4 flex align-items-center gap-2">
      <Button
          label="Skapa ny anteckning"
          icon="pi pi-plus"
          class="p-button-primary"
          @click="createNote"
        />
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
          <InputText v-model="noteForm.title" v-tooltip.focus.top="'Ange titel till anteckningen'" placeholder="Titel" required />
        </div>
        <div class="field mb-3">
          <Textarea v-model="noteForm.content" v-tooltip.focus.top="'Ange innehållet i anteckningen'" placeholder="Innehåll" rows="5" required />
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
      </div>
  <Accordion multiple>
    <AccordionTab
      v-for="note in sortedNotes"
      :key="note.id"
    >
      <template #header>
        <div class="flex justify-between items-center">
          <div>
            <div class="text-lg font-semibold">{{ note.title }}</div>
            <small class="text-sm text-gray-500">
              <span v-if="!isSameTime(note.updatedAt, note.createdAt)">
                Uppdaterad för {{ fromNow(note.updatedAt) }}
              </span>
              <span v-else>
                Skapad för {{ fromNow(note.createdAt) }}
              </span>
              <h5>
                Skriven av: {{ note.createdBy?.firstName || 'Okänd' }}<br>
              </h5>
            </small>
          </div>
        </div>
      </template>

      <!-- Innehåll -->
      <p class="mt-2">{{ note.content }}</p>

      <!-- Visa alla bilder -->
      <div v-if="note.images?.length && currentUser" class="mt-3 flex gap-3 flex-wrap">
        <div
          v-for="image in note.images"
          :key="image.id"
          class="relative"
          style="display: inline-block;"
        >
          <img
            :src="image.url"
            alt="Bifogad bild"
            style="max-width: 200px; border-radius: 6px; cursor: pointer;"
            @click="() => { selectedImage = image.url }"
          />
          <Button
            v-if="note.createdBy?.id === currentUser.id"
            @click="deleteImage(image.id, note)"
            icon="pi pi-times" severity="danger" variant="text" rounded aria-label="Cancel"
          >
          </Button>
        </div>
      </div>

      <small class="p-text-secondary block mt-2">
        Skapad: {{ formatDate(note?.createdAt) || 'Okänd' }}<br />
        Uppdaterad: {{ formatDate(note?.updatedAt) || 'Okänd' }}
      </small>

      <div class="p-mt-3 flex gap-2">
        <Button
          label="Redigera"
          icon="pi pi-pencil"
          class="p-button-warning p-button-sm"
          @click="updateNote(note)"
        />
        <Button
          label="Ta bort"
          icon="pi pi-trash"
          class="p-button-danger p-button-sm"
          @click="confirmDelete(note)"
        />
      </div>
    </AccordionTab>
  </Accordion>

  <Dialog
    v-model:visible="dialogVisible"
    :modal="true"
    :closable="true"
    @hide="selectedImage = null"
    style="max-width: 90vw; max-height: 90vh;"
  >
    <img
      :src="selectedImage"
      alt="Fullstor bild"
      style="width: 100%; height: auto; border-radius: 6px;"
    />
  </Dialog>
</template>


<script setup>
import { defineProps, ref, computed, onMounted } from 'vue'
import Accordion from 'primevue/accordion'
import AccordionTab from 'primevue/accordiontab'
import Button from 'primevue/button'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import relativeTime from 'dayjs/plugin/relativeTime'
import axios from 'axios'
import { hideLoading, showLoading } from '@/stores/loadingStore'
import { useToaster } from '@/stores/toastStore'


dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.extend(relativeTime)

const authHeader = localStorage.getItem('auth')
const config = {
  headers: { Authorization: authHeader }
}
const { showSuccessToast, showErrorToast, showInfoToast } = useToaster()

const selectedImage = ref(null);

const notes = ref([])
const noteForm = ref({ title: '', content: '', images: [] })
const editingNote = ref(null)
const showNoteForm = ref(false)
const selectedFile = ref(null)
const currentUser = ref(null)
const users = ref([])

const props = defineProps({
  currentUser: Object
})


const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const confirm = useConfirm()

const sortedNotes = computed(() => {
  return [...notes.value].sort((a, b) => {
    const aTime = new Date(a.updatedAt || a.createdAt).getTime()
    const bTime = new Date(b.updatedAt || b.createdAt).getTime()
    return bTime - aTime // senaste först
  })
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
    showLoading()
    let noteId = null;
    if (editingNote.value) {
      const res = await axios.put(
        `${apiUrl}/api/notes/update/${editingNote.value.id}`,
        noteForm.value,
        config
      );
      noteId = res.data.id;
      showSuccessToast('Anteckning uppdaterad!')

    } else {
      const res = await axios.post(
        `${apiUrl}/api/notes/create`,
        noteForm.value,
        config
      );
      noteId = res.data.id;
      notes.value.push(res.data);
      showSuccessToast('Anteckning skapad!')
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
        showErrorToast('Fel vid spara/redigera')
      } finally {
        hideLoading()
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
    showLoading()
    await axios.delete(`${apiUrl}/api/notes/${id}`, config)
    notes.value = notes.value.filter(n => n.id !== id)
    showSuccessToast('Tagit bort anteckningen')
  } catch (err) {
    showErrorToast('Gick inte att ta bort anteckning')
  } finally {
    hideLoading()
  }
}

// Computed för dialogens synlighet
const dialogVisible = computed({
  get() {
    return selectedImage.value !== null
  },
  set(value) {
    if (!value) selectedImage.value = null
  }
})

function isSameTime(t1, t2) {
  // jämför ISO-strängar exakt
  return dayjs(t1).isSame(dayjs(t2))
}

function formatDate(dateString) {
  if (!dateString) return 'Okänd';
  return dayjs.utc(dateString).tz('Europe/Stockholm').format('YYYY-MM-DD HH:mm');
}

function fromNow(dateString) {
  return dateString
    ? dayjs.utc(dateString).tz('Europe/Stockholm').fromNow()
    : 'okänt';
}

const confirmDelete = (note) => {
  confirm.require({
    message: `Vill du verkligen ta bort anteckningen "${note.title}"?`,
    header: 'Bekräfta borttagning',
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: 'Ja',
    rejectLabel: 'Nej',
    acceptClass: 'p-button-danger',
    accept: () => {
      deleteNote(note.id)
    }
  })
}

const deleteImage = async (imageId, note) => {
  if (!window.confirm('Är du säker på att du vill ta bort denna bild?')) return;

  try {
    await axios.delete(`${apiUrl}/api/images/${imageId}`, {
      headers: { Authorization: localStorage.getItem('auth') },
    });

    // Uppdatera images lokalt
    const index = note.images.findIndex(img => img.id === imageId);
    if (index !== -1) note.images.splice(index, 1);
  } catch (err) {

  }
};

const fetchNotes = async () => {
  try {
    showLoading()
    const res = await axios.get(`${apiUrl}/api/notes/all`, config)
    notes.value = res.data || []
  } catch (err) {
    showErrorToast('Gick inte att hämta anteckningar')
  } finally {
    hideLoading()
  }
}



onMounted(async () => {
  const meRes = await axios.get(`${apiUrl}/api/auth/me`, config)
  currentUser.value = meRes.data

  try {
  showInfoToast('Hämtar data..')
  const usersRes = await axios.get(`${apiUrl}/api/users/`, config)
  users.value = usersRes.data

  fetchNotes()
  showSuccessToast('Data hämtad!')
} catch (err) {
  console.error("Fel vid hämtning av data:", err)
  showErrorToast('Gick inte att hämta data')
}

})

</script>
