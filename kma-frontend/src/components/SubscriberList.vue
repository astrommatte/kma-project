<template>
  <DataTable :value="subscribers" dataKey="id" class="p-datatable-sm" responsiveLayout="scroll">
    <Column field="firstName" header="Förnamn" />
    <Column field="lastName" header="Efternamn" />
    <Column field="email" header="Email" />
    <Column field="type" header="Typ" />
    <Column header="Åtgärder" :style="{ width: '150px' }">
      <template #body="slotProps">
        <Button
          icon="pi pi-pencil"
          class="p-button-rounded p-button-text p-button-info p-mr-2"
          aria-label="Redigera"
          @click="$emit('edit-subscriber', slotProps.data)"
        />
        <Button
          icon="pi pi-trash"
          class="p-button-rounded p-button-text p-button-danger"
          aria-label="Ta bort"
          @click="confirmDelete(slotProps.data)"
        />
      </template>
    </Column>
  </DataTable>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import { useConfirm } from 'primevue/useconfirm'

const confirm = useConfirm()
const emit = defineEmits(['edit-subscriber', 'delete-subscriber'])

const confirmDelete = (subscriber) => {
  confirm.require({
    message: `Vill du verkligen ta bort ${subscriber.firstName} ${subscriber.lastName}?`,
    header: 'Bekräfta borttagning',
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: 'Ja',
    rejectLabel: 'Nej',
    acceptClass: 'p-button-danger',
    accept: () => {
      emit('delete-subscriber', subscriber)
    }
  })
}

defineProps({
  subscribers: Array
})
</script>
