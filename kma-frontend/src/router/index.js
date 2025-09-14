import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue' // skapa senare
import NotesList from '@/components/NotesList.vue'
import AdminView from '@/views/AdminView.vue'
import Todo from '@/components/Todo.vue'

const routes = [
  {
    path: '/',
    name: 'login',
    component: LoginView,
    meta: { hideNavbar: true } // <-- här säger vi att navbar ska döljas
  },
  {
    path: '/notes',
    name: 'notes',
    component: NotesList
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView
  },
  {
    path: '/todos',
    name: 'Todos',
    component: Todo
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
