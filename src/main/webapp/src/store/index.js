import { createStore } from 'vuex'
import language from './modules/language'
import principal from './modules/principal'
import createPersistedState from 'vuex-persistedstate'

const store = createStore({
  state: {
    loginModalOpened: null,
    modalOpened: null,
    nextRoute: null,
    returnRoute: null,
    cssClass: null
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
    },
    setCssClass (state, cssClass) {
      state.cssClass = cssClass
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
    },
    getCssClass: (state) => {
      return state.cssClass
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
