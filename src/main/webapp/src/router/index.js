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
      roles: ['ROLE_USER'],
      cssClass: 'site'
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
      navBarView: 'navBarDefault',
      cssClass: 'admin'
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
      },
      {
        path: '/configuration',
        name: 'AdminConfiguration',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/configuration/AdminConfiguration.vue'),
        meta: {
          titleKey: 'configuration.title'
        }
      },
      {
        path: '/health',
        name: 'AdminHealth',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/health/AdminHealth.vue'),
        meta: {
          titleKey: 'health.title'
        }
      },
      {
        path: '/metrics',
        name: 'AdminMetrics',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/metrics/AdminMetrics.vue'),
        meta: {
          titleKey: 'metrics.title'
        }
      },
      {
        path: '/audits',
        name: 'AdminAudits',
        component: () => import(/* webpackChunkName: "administration" */ '../views/admin/audits/AdminAudits.vue'),
        meta: {
          titleKey: 'audits.title'
        }
      },
      {
        path: '/organization',
        name: 'AdminEntityOrganization',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/organization/Organization.vue'),
        meta: {
          titleKey: 'organization.home.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/organization/:id',
        name: 'AdminEntityOrganizationDetails',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/organization/OrganizationDetail.vue'),
        meta: {
          titleKey: 'organization.detail.title',
          cssClass: 'entity'
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
        store.commit('setReturnRoute', store.getters.getNextRoute)
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
    store.commit('setCssClass', to.meta.cssClass)
    document.title = t(to.meta.titleKey || DEFAULT_TITLE)
  })
})

export default router
