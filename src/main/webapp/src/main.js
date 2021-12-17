import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import store from './store'
import NavbarDirective from './directives/NavbarDirective.js'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import './assets/styles/main.css'
import './assets/styles/mdioverbootstrap.css'
import './assets/styles/normalize.css'
import '@fortawesome/fontawesome-free/css/solid.css'
import '@fortawesome/fontawesome-free/js/solid.js'
import '@fortawesome/fontawesome-free/css/regular.css'
import '@fortawesome/fontawesome-free/js/regular.js'
import '@fortawesome/fontawesome-free/css/fontawesome.css'
import '@fortawesome/fontawesome-free/js/fontawesome.js'

createApp(App)
  .directive('active-menu', NavbarDirective)
  .use(store).use(router).use(i18n).mount('#app')
