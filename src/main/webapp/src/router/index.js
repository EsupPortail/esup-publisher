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
    path: '/error',
    name: 'Error',
    component: () => import(/* webpackChunkName: "error" */ '../views/error/Error.vue'),
    meta: {
      titleKey: 'errors.title',
      requireLogin: true,
      cssClass: 'site'
    }
  },
  {
    path: '/accessdenied',
    name: 'AccessDenied',
    component: () => import(/* webpackChunkName: "error" */ '../views/error/AccessDenied.vue'),
    meta: {
      titleKey: 'errors.title',
      requireLogin: true,
      cssClass: 'site'
    }
  },
  {
    path: '/manager',
    name: 'Manager',
    component: () => import(/* webpackChunkName: "manager" */ '../views/manager/Manager.vue'),
    redirect: { name: 'Home' },
    meta: {
      requireLogin: true,
      roles: ['ROLE_USER'],
      navBarView: 'navBarManager',
      cssClass: 'site'
    },
    children: [
      {
        path: '/treeview',
        name: 'Treeview',
        component: () => import(/* webpackChunkName: "manager" */ '../views/manager/treeview/Treeview.vue'),
        meta: {
          titleKey: 'manager.treeview.title',
          roles: ['ROLE_USER'],
          cssClass: 'manager',
          managerCssClass: 'treeview'
        },
        children: [
          {
            path: 'details/:ctxType/:ctxId',
            name: 'TreeviewCtxDetails',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/treeview/ctxDetails/CtxDetails.vue'),
            meta: {
              cssClass: 'treeview',
              managerCssClass: 'treeview ctxDetails'
            }
          }
        ]
      },
      {
        path: '/publish/:id?',
        name: 'Publish',
        component: () => import(/* webpackChunkName: "manager" */ '../views/manager/publish/Publish.vue'),
        meta: {
          titleKey: 'manager.publish.title',
          navBarView: 'navBarPublish',
          cssClass: 'publish'
        },
        children: [
          {
            path: 'publisher',
            name: 'PublishPublisher',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/publish/publisher/Publisher.vue'),
            meta: {
              managerCssClass: 'publish.publisher'
            }
          },
          {
            path: 'classification',
            name: 'PublishClassification',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/publish/classification/Classification.vue'),
            meta: {
              managerCssClass: 'publish publish.classification'
            }
          },
          {
            path: 'content',
            name: 'PublishContent',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/publish/content/Content.vue'),
            meta: {
              managerCssClass: 'publish publish.content'
            }
          },
          {
            path: 'targets',
            name: 'PublishTargets',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/publish/targets/Targets.vue'),
            meta: {
              managerCssClass: 'publish publish.targets'
            }
          }
        ]
      },
      {
        path: '/contents',
        name: 'Contents',
        component: () => import(/* webpackChunkName: "manager" */ '../views/manager/contents/Contents.vue'),
        meta: {
          titleKey: 'manager.contents.title',
          cssClass: 'contents'
        },
        children: [
          {
            path: 'owned',
            name: 'ContentsOwned',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/contents/owned/Owned.vue'),
            meta: {
              managerCssClass: 'contents owned'
            }
          },
          {
            path: 'managed',
            name: 'ContentsManaged',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/contents/managed/Managed.vue'),
            meta: {
              managerCssClass: 'contents managed'
            }
          },
          {
            path: 'pending',
            name: 'ContentPending',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/contents/pending/Pending.vue'),
            meta: {
              managerCssClass: 'contents pending'
            }
          },
          {
            path: 'detail/:id',
            name: 'ContentDetail',
            component: () => import(/* webpackChunkName: "manager" */ '../views/manager/contents/details/ContentDetail.vue'),
            meta: {
              titleKey: 'manager.contents.details.title',
              roles: ['ROLE_USER'],
              managerCssClass: 'contents details'
            }
          }
        ]
      }
    ]
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
          roles: ['ROLE_USER'],
          titleKey: 'organization.home.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/organization/:id',
        name: 'AdminEntityOrganizationDetails',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/organization/OrganizationDetail.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'organization.detail.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/filter',
        name: 'AdminEntityFilter',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/filter/Filter.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'filter.home.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/filter/:id',
        name: 'AdminEntityFilterDetails',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/filter/FilterDetail.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'filter.detail.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/reader',
        name: 'AdminEntityReader',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/reader/Reader.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'reader.home.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/reader/:id',
        name: 'AdminEntityReaderDetails',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/reader/ReaderDetail.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'reader.detail.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/redactor',
        name: 'AdminEntityRedactor',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/redactor/Redactor.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'redactor.home.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/redactor/:id',
        name: 'AdminEntityRedactorDetails',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/redactor/RedactorDetail.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'redactor.detail.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/publisher',
        name: 'AdminEntityPublisher',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/publisher/Publisher.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'publisher.home.title',
          cssClass: 'entity'
        }
      },
      {
        path: '/publisher/:id',
        name: 'AdminEntityPublisherDetails',
        component: () => import(/* webpackChunkName: "administration" */ '../views/entities/publisher/PublisherDetail.vue'),
        meta: {
          roles: ['ROLE_USER'],
          titleKey: 'publisher.detail.title',
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
  store.commit('setPreviousRoute', {
    name: from.name,
    params: from.params,
    meta: from.meta
  })
  store.commit('setNextRoute', {
    name: to.name,
    params: to.params,
    meta: to.meta
  })
  if (to.matched.some(record => record.meta.requireLogin)) {
    if (!PrincipalService.isAuthenticated() && !store.getters.getLoginModalOpened) {
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
    document.title = t(to.meta.titleKey || DEFAULT_TITLE)
  })
})

export default router
