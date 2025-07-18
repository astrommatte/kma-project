import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import './assets/main.css'

import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';
import Button from 'primevue/button'; // Example component
import Select from 'primevue/select'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea';
import Listbox from 'primevue/listbox';
import ConfirmDialog from 'primevue/confirmdialog'
import ConfirmationService from 'primevue/confirmationservice'
import Checkbox from 'primevue/checkbox'
import Dialog from 'primevue/dialog'
import FileUpload from 'primevue/fileupload'
import ProgressSpinner from 'primevue/progressspinner';

import 'primeicons/primeicons.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue, {
    theme: {
        preset: Aura
    }
});
app.use(ConfirmationService)

app.component('Button', Button);
app.component('Select', Select);
app.component('InputText', InputText);
app.component('Textarea', Textarea);
app.component('Listbox', Listbox);
app.component('ConfirmDialog', ConfirmDialog)
app.component('Checkbox', Checkbox)
app.component('Dialog', Dialog)
app.component('FileUpload', FileUpload)
app.component('ProgressSpinner', ProgressSpinner)

app.mount('#app')
