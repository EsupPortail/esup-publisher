import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import store from './store'
import NavbarDirective from './directives/NavbarDirective.js'
import CanModerateDirective from './directives/CanModerateDirective.js'
import HasRoleDirective from './directives/HasRoleDirective.js'
import DisableClickDirective from './directives/DisableClickDirective.js'
import TooltipDirective from './directives/TooltipDirective.js'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import './assets/styles/main.css'
import './assets/styles/mdioverbootstrap.css'
import './assets/styles/normalize.css'
import './assets/styles/fonts.scss'
import '@fortawesome/fontawesome-free/css/solid.css'
import '@fortawesome/fontawesome-free/js/solid.js'
import '@fortawesome/fontawesome-free/css/regular.css'
import '@fortawesome/fontawesome-free/js/regular.js'
import '@fortawesome/fontawesome-free/css/fontawesome.css'
import '@fortawesome/fontawesome-free/js/fontawesome.js'

const app = createApp(App)

app.directive('active-menu', NavbarDirective)
  .directive('can-moderate', CanModerateDirective)
  .directive('has-role', HasRoleDirective)
  .directive('disable-click', DisableClickDirective)
  .directive('tooltip', TooltipDirective)
  .use(store).use(router).use(i18n).mount('#app')

// Plus nécessaire à partir de Vue 3.3
app.config.unwrapInjectedRef = true
