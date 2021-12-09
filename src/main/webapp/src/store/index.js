import { createStore } from 'vuex'
import language from './modules/language'
import principal from './modules/principal'
import createPersistedState from 'vuex-persistedstate'

const store = createStore({
  state: {
    loginModalOpened: null,
    modalOpened: null,
    nextRoute: null,
    returnRoute: null
  },
  mutations: {
    setModalOpened (state, modalOpened) {
      state.modalOpened = modalOpened
    },
    setLoginModalOpened (state, loginModalOpened) {
      state.loginModalOpened = loginModalOpened
    },
    setNextRoute (state, nextRoute) {
      state.nextRoute = nextRoute
    },
    setReturnRoute (state, returnRoute) {
      state.returnRoute = returnRoute
    }
  },
  getters: {
    getModalOpened: (state) => {
      return state.modalOpened
    },
    getLoginModalOpened: (state) => {
      return state.loginModalOpened
    },
    getNextRoute: (state) => {
      return state.nextRoute
    },
    getReturnRoute: (state) => {
      return state.returnRoute
    }
  },
  actions: {
  },
  modules: {
    language,
    principal
  },
  plugins: [createPersistedState()]
})

export default store
