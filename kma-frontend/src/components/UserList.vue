<template>
  <DataTable :value="users" dataKey="id" class="p-datatable-sm" responsiveLayout="scroll">
    <Column field="firstName" header="Förnamn" />
    <Column field="lastName" header="Efternamn" />
    <Column field="email" header="Email" />

    <Column header="Åtgärder" :style="{ width: '150px' }">
      <template #body="slotProps">
        <Button
          icon="pi pi-pencil"
          class="p-button-rounded p-button-text p-button-info p-mr-2"
          aria-label="Redigera"
          @click="$emit('edit-user', slotProps.data)"
        />
        <Button
          icon="pi pi-trash"
          class="p-button-rounded p-button-text p-button-danger"
          aria-label="Ta bort"
          @click="$emit('delete-user', slotProps.data)"
        />
      </template>
    </Column>
  </DataTable>
  <div class="p-mt-3">
    <Button
      label="Skapa ny"
      icon="pi pi-plus"
      class="p-button-primary"
      @click="$emit('create-user')"
    />
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'

defineProps({
  users: Array
})

defineEmits(['create-user', 'edit-user', 'delete-user'])
</script>
