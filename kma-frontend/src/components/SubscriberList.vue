<template>

<div class="p-d-flex p-ai-center p-jc-between p-gap-3 p-bg-gray-800 p-p-3" style="color: white;">
  <Select
    :options="users"
    optionLabel="firstName"
    optionValue="id"
    placeholder="Välj användare"
    class="p-inputtext-sm"
    @change="handleUserSelect"
    :filter="true"
    filterPlaceholder="Sök användare..."
    :showClear="true"
    style="min-width: 200px;"
  />

  <h5 class="text-sm m-0">
    I listan visas kunder för den valda användaren ovan.
  </h5>
</div>


    <!-- 🔍 Sökfält -->
  <div class="p-inputgroup mb-3">
    <span class="p-inputgroup-addon">
      <i class="pi pi-search"></i>
    </span>
    <InputText
      type="text"
      v-model="searchTerm"
      placeholder="Sök kund i listan.."
      class="p-inputtext p-component"
    />
  </div>

  <DataTable
    :value="filteredSubscribers"
    dataKey="id"
    class="p-datatable-sm"
    responsiveLayout="scroll"
  >
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
import { defineProps, defineEmits, ref, computed } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import { useConfirm } from 'primevue/useconfirm'

const confirm = useConfirm()
const emit = defineEmits(['edit-subscriber', 'delete-subscriber','user-selected'])

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

const props = defineProps({
  subscribers: Array,
  selectedUser: Object,
  users: Array,
})

const searchTerm = ref('')

const filteredSubscribers = computed(() => {
  if (!searchTerm.value) return props.subscribers

  return props.subscribers.filter(sub =>
    [sub.firstName, sub.lastName, sub.email]
      .join(' ')
      .toLowerCase()
      .includes(searchTerm.value.toLowerCase())
  )
})

const handleUserSelect = (e) => {
  // e.value är det valda id:t från Dropdown
  const selected = props.users.find(u => u.id === e.value)
  emit('user-selected', selected)
}
</script>

<style scoped>
*{
  margin-top: 10px;
  margin-bottom: 10px;
}

</style>
