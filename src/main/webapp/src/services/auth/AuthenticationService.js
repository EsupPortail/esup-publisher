import PrincipalService from './PrincipalService'
import store from '@/store/index.js'
import router from '@/router/index.js'
import FetchWrapper from '../util/FetchWrapper'

class AuthenticationService {
  login () {
    return new Promise((resolve, reject) => {
      FetchWrapper.getJsonP('app/login').then(() => {
        PrincipalService.identify(true).then(account => {
          if (account !== undefined && account.user.langKey !== undefined && account.user.langKey !== null) {
            store.commit('setLang', account.user.langKey)
          }
          resolve(account)
        }).catch(err => {
          reject(err)
        })
      }).catch(err => {
        this.logout()
        PrincipalService.authenticate(null)
        reject(err)
      })
    })
  }

  logout () {
    return new Promise((resolve) => {
      FetchWrapper.postJson('api/logout').then(response => {
        store.commit('clearAll')
        resolve(response)
      }).catch(() => {
      })
    })
  }

  authorize () {
    return PrincipalService.identify()
      .then(() => {
        const isAuthenticated = PrincipalService.isAuthenticated()
        if (store.getters.getNextRoute.meta.roles && store.getters.getNextRoute.meta.roles.length > 0 && !PrincipalService.isInAnyRole(store.getters.getNextRoute.meta.roles)) {
          if (isAuthenticated) {
            // user is signed in but not authorized for desired state
            router.push({ name: 'AccessDenied' })
          } else {
            // user is not authenticated. stow the state they wanted before you
            // send them to the signin state, so you can return them when you're done
            store.commit('setReturnRoute', store.getters.getNextRoute)

            // now, send them to the signin state so they can log in
            router.push({ name: 'Login' })
          }
        }
      })
  }
}

export default new AuthenticationService()
