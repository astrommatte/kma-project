<template>
  <div class="todo-container">
    <h3>🛒 Inköpslista</h3>

    <!-- Lägg till nytt todo -->
    <div class="todo-input">
      <InputText v-model="newTask" placeholder="Lägg till..." />
      <Button icon="pi pi-plus" @click="createTodo" />
    </div>

    <!-- Lista todos -->
    <ul class="todo-list">
      <li
        v-for="todo in sortedTodos"
        :key="todo.id"
        class="todo-item"
        :class="{ completed: todo.completed }"
        @click="toggleCompleted(todo.id)"
      >
        <span>{{ todo.task }}</span>
        <Button
          icon="pi pi-trash"
          class="p-button-sm p-button-danger"
          @click.stop="deleteTodo(todo.id)"
        />
      </li>
    </ul>
  </div>
</template>
  
  <script setup>
  import { ref, onMounted, computed } from 'vue'
  import axios from 'axios'
  import InputText from 'primevue/inputtext'
  import Button from 'primevue/button'
  import Checkbox from 'primevue/checkbox'
  
  const todos = ref([])
  const newTask = ref('')
  
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
  
  const authHeader = localStorage.getItem('auth')

// Hämta todos
const fetchTodos = async () => {
  try {
    const res = await axios.get(`${apiUrl}/api/todos`, {
      headers: { Authorization: authHeader }
    })
    todos.value = res.data
  } catch (err) {
   
  }
}

// Skapa nytt todo
const createTodo = async () => {
  if (!newTask.value.trim()) return
  try {
    const res = await axios.post(
      `${apiUrl}/api/todos`,
      { task: newTask.value },
      { headers: { Authorization: authHeader } }
    )
    todos.value.push(res.data)
    newTask.value = ''
  } catch (err) {
    console.error('Error creating todo:', err)
  }
}

// Toggle completed
const toggleCompleted = async (id) => {
  try {
    const res = await axios.put(
      `${apiUrl}/api/todos/${id}/complete`,
      {},
      { headers: { Authorization: authHeader } }
    )
    const index = todos.value.findIndex(t => t.id === id)
    if (index !== -1) todos.value[index] = res.data
  } catch (err) {
    console.error('Error toggling todo:', err)
  }
}

// Ta bort todo
const deleteTodo = async (id) => {
  try {
    await axios.delete(`${apiUrl}/api/todos/${id}`, {
      headers: { Authorization: authHeader }
    })
    todos.value = todos.value.filter(t => t.id !== id)
  } catch (err) {
    console.error('Error deleting todo:', err)
  }
}
  
  // Sortera senaste först
  const sortedTodos = computed(() => {
    return [...todos.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  })
  
  onMounted(fetchTodos)
  </script>
  
  <style scoped>
  .todo-container {
    max-width: 500px;
    margin: 2rem auto;
    background: #fff;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }
  
  .todo-container h3 {
    font-size: 1.6rem;
    margin-bottom: 1rem;
    color: #333;
  }
  
  .todo-input {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1.5rem;
  }
  
  .todo-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  
  .todo-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #f8f9fa;
    padding: 0.75rem 1rem;
    border-radius: 8px;
    margin-bottom: 0.5rem;
    transition: background 0.2s ease, transform 0.1s ease;
    cursor: pointer;
  }
  
  .todo-item:hover {
    background: #eef1f5;
    transform: scale(1.01);
  }
  
  .todo-item.completed span {
    text-decoration: line-through;
    color: #999;
    font-style: italic;
  }
  </style>
  