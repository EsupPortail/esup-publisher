import store from '@/store/index.js'
import AccountService from './AccountService'

class PrincipalService {
  identify (force) {
    return new Promise(resolve => {
      const identity = store.getters.getIdentity
      if (force === true) {
        store.commit('setIdentity', undefined)
      }

      // check and see if we have retrieved the identity data from the server.
      // if we have, reuse it by immediately resolving
      if (identity !== undefined && identity != null) {
        resolve(identity)
      }

      // retrieve the identity data from the server, update the identity object, and then resolve.
      AccountService.account().then(account => {
        store.commit('setIdentity', account)
        store.commit('setAuthenticated', true)
        resolve(identity)
      }).catch(() => {
        store.commit('setIdentity', null)
        store.commit('setAuthenticated', false)
        resolve(identity)
      })
    })
  }

  authenticate (identity) {
    store.commit('setIdentity', identity)
    store.commit('setAuthenticated', identity !== null)
  }

  isInAnyRole (roles) {
    const identity = store.getters.getIdentity
    const authenticated = store.getters.getAuthenticated
    if (!authenticated || identity === undefined || !identity.roles) {
      return false
    }

    for (let i = 0; i < roles.length; i++) {
      if (this.isInRole(roles[i])) {
        return true
      }
    }
    return false
  }

  isInRole (role) {
    const identity = store.getters.getIdentity
    const authenticated = store.getters.getAuthenticated
    if (!authenticated || identity === undefined || !identity.roles) {
      return false
    }
    return identity.roles.indexOf(role) !== -1
  }

  isAuthenticated () {
    return store.getters.getAuthenticated
  }

  isIdentityResolved () {
    return typeof store.getters.getIdentity !== 'undefined'
  }
}

export default new PrincipalService()
