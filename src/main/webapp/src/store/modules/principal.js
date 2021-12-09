const state = {
  _identity: null,
  _authenticated: false
}

const mutations = {
  setIdentity (state, identity) {
    state._identity = identity
  },
  setAuthenticated (state, authenticated) {
    state._authenticated = authenticated
  },
  clearAll (state) {
    state._identity = null
    state._authenticated = false
  }
}
const getters = {
  getIdentity: (state) => {
    return state._identity
  },
  getAuthenticated: (state) => {
    return state._authenticated
  }
}

export default {
  state,
  mutations,
  getters
}
