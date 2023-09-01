import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import i18n from './i18n';
import store from './store';
import NavbarDirective from './directives/NavbarDirective.js';
import CanModerateDirective from './directives/CanModerateDirective.js';
import CanEditTargetsDirective from './directives/CanEditTargetsDirective.js';
import CanEditDirective from './directives/CanEditDirective.js';
import CanDeleteDirective from './directives/CanDeleteDirective.js';
import CanHighlightDirective from './directives/CanHighlightDirective.js';
import CanCreateInDirective from './directives/CanCreateInDirective.js';
import HasAnyRoleDirective from './directives/HasAnyRoleDirective.js';
import HasRoleDirective from './directives/HasRoleDirective.js';
import DisableClickDirective from './directives/DisableClickDirective.js';
import TooltipDirective from './directives/TooltipDirective.js';
import ToastPlugin from './components/toaster';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';
import './assets/styles/main.scss';
import './assets/styles/normalize.scss';
import './assets/styles/fonts.scss';
import './assets/styles/ck-content-style.scss';
import '@fortawesome/fontawesome-free/css/all.css'; // Utilisation de Font Awesome via Web Fonts with CSS
import '@gip-recia/js-tree';
import '@gip-recia/subject-infos';
import '@gip-recia/subject-search-button';
import '@gip-recia/evaluator';
import '@gip-recia/color-palette-picker';

const app = createApp(App);

app
  .directive('active-menu', NavbarDirective)
  .directive('can-moderate', CanModerateDirective)
  .directive('can-edit-targets', CanEditTargetsDirective)
  .directive('can-edit', CanEditDirective)
  .directive('can-delete', CanDeleteDirective)
  .directive('can-highlight', CanHighlightDirective)
  .directive('can-create-in', CanCreateInDirective)
  .directive('has-any-role', HasAnyRoleDirective)
  .directive('has-role', HasRoleDirective)
  .directive('disable-click', DisableClickDirective)
  .directive('tooltip', TooltipDirective)
  .use(store)
  .use(router)
  .use(i18n)
  .use(ToastPlugin)
  .mount('#app');

// Plus nécessaire à partir de Vue 3.3
app.config.unwrapInjectedRef = true;
