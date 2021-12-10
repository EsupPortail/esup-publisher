import { nextTick } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import AuthenticationService from '../services/auth/AuthenticationService'
import PrincipalService from '../services/auth/PrincipalService'
import store from '@/store/index.js'
import i18n from '../i18n'

const { t } = i18n.global
const DEFAULT_TITLE = 'global.title'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import(/* webpackChunkName: "home" */ '../views/Home.vue'),
    meta: {
      titleKey: DEFAULT_TITLE,
      requireLogin: true,
      roles: ['ROLE_USER']
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import(/* webpackChunkName: "login" */ '../views/account/login/Login.vue'),
    meta: {
      titleKey: 'login.title',
      requireLogin: false
    }
  },
  {
    path: '/accessdenied',
    name: 'AccessDenied',
    component: () => import(/* webpackChunkName: "error" */ '../views/error/AccessDenied.vue'),
    meta: {
      titleKey: 'errors.title',
      requireLogin: true
    }
  },
  {
    path: '/administration',
    name: 'Administration',
    component: () => import(/* webpackChunkName: "administration" */ '../views/admin/Admin.vue'),
    meta: {
      requireLogin: true,
      roles: ['ROLE_ADMIN'],
      navBarView: 'navBarDefault'
    },
    children: [
      {
        path: '',
        name: 'AdminMain',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/main/AdminMain.vue'),
        meta: {
          titleKey: 'admin.title'
        }
      },
      {
        path: '/docs',
        name: 'AdminDocs',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/docs/AdminDocs.vue'),
        meta: {
          titleKey: 'global.menu.admin.apidocs'
        }
      },
      {
        path: '/logs',
        name: 'AdminLogs',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/logs/AdminLogs.vue'),
        meta: {
          titleKey: 'logs.title'
        }
      }
    ]
  },
  // Sinon redirection vers Home
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  store.commit('setNextRoute', {
    name: to.name,
    params: to.params,
    meta: to.meta
  })
  if (to.matched.some(record => record.meta.requireLogin)) {
    if (!PrincipalService.isAuthenticated() && !store.getters.getModalOpened) {
      AuthenticationService.login().then(() => {
        next()
      }).catch(() => {
        store.commit('setLoginModalOpened', true)
        next({
          path: '/login'
        })
      })
    } else {
      AuthenticationService.authorize().then(() => {
        next()
      })
    }
  } else {
    next()
  }
})

router.afterEach((to) => {
  nextTick(() => {
    document.title = t(to.meta.titleKey || DEFAULT_TITLE)
  })
})

export default router
