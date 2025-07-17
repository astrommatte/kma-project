<template>
      <div class="mb-4 flex align-items-center gap-2">
      <label for="showNotes">Visa anteckningar (som alla kan se)</label>
      <Checkbox v-model="showNotes" binary />
    </div>
  <Accordion multiple>
    <AccordionTab
     v-if="showNotes"
      v-for="note in sortedNotes"
      :key="note.id"
    >
      <template #header>
        <div class="flex justify-between items-center">
          <div>
            <div class="text-lg font-semibold">{{ note.title }}</div>
            <small class="text-sm text-gray-500">
              <span v-if="note.updatedAt !== note.createdAt">
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
      <div v-if="note.imageIds?.length" class="mt-3 flex gap-3 flex-wrap">
        <div
          v-for="id in note.imageIds"
          :key="id"
          class="relative"
          style="display: inline-block;"
        >
          <img
            :src="`${apiUrl}/api/images/${id}`"
            alt="Bifogad bild"
            style="max-width: 200px; border-radius: 6px; cursor: pointer;"
            @click="() => { selectedImage = `${import.meta.env.VITE_API_URL}/api/images/${id}` }"
          />
          <Button
            v-if="note.createdBy?.id === currentUser.id"
            @click="deleteImage(id, note)"
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
          @click="$emit('edit-note', note)"
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


  <div class="p-mt-3">
    <Button
      v-if="showNotes"
      label="Skapa ny anteckning"
      icon="pi pi-plus"
      class="p-button-primary"
      @click="$emit('create-note')"
    />
  </div>
</template>


<script setup>
import { defineProps, defineEmits, ref, computed } from 'vue'
import Accordion from 'primevue/accordion'
import AccordionTab from 'primevue/accordiontab'
import Button from 'primevue/button'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import relativeTime from 'dayjs/plugin/relativeTime'
import axios from 'axios'

dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.extend(relativeTime)

const showNotes = ref(false)
const selectedImage = ref(null);

const props = defineProps({
  notes: Array,
  currentUser: Object
})

const emit = defineEmits(['create-note', 'edit-note', 'delete-note'])

const apiUrl = import.meta.env.VITE_API_URL;
const confirm = useConfirm()

const sortedNotes = computed(() => {
  return [...props.notes].sort((a, b) => {
    const aTime = new Date(a.updatedAt || a.createdAt).getTime()
    const bTime = new Date(b.updatedAt || b.createdAt).getTime()
    return bTime - aTime // senaste först
  })
})

// Computed för dialogens synlighet
const dialogVisible = computed({
  get() {
    return selectedImage.value !== null
  },
  set(value) {
    if (!value) selectedImage.value = null
  }
})

function formatDate(dateString) {
  if (!dateString) return 'Okänd';
  return dayjs.utc(dateString).format('YYYY-MM-DD HH:mm');
}

function fromNow(dateString) {
  return dateString ? dayjs(dateString).fromNow() : 'okänt'
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
      emit('delete-note', note.id)
    }
  })
}

const deleteImage = async (imageId, note) => {
  if (!window.confirm('Är du säker på att du vill ta bort denna bild?')) return;

  try {
    await axios.delete(`${import.meta.env.VITE_API_URL}/api/images/${imageId}`, {
      headers: { Authorization: localStorage.getItem('auth') },
    });

    // Uppdatera imageIds lokalt
    const index = note.imageIds.indexOf(imageId);
    if (index !== -1) note.imageIds.splice(index, 1);
  } catch (err) {
    console.error('Kunde inte ta bort bilden:', err);
  }
};

</script>
