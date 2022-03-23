import { createStore } from 'vuex'
import language from './modules/language'
import principal from './modules/principal'
import CookieUtils from '@/services/util/CookieUtils'

const STORE_KEY = 'store'

const store = createStore({
  state: {
    loginModalOpened: null,
    previousRoute: null,
    nextRoute: null,
    returnRoute: null
  },
  mutations: {
    setLoginModalOpened (state, loginModalOpened) {
      state.loginModalOpened = loginModalOpened
    },
    setPreviousRoute (state, previousRoute) {
      state.previousRoute = previousRoute
    },
    setNextRoute (state, nextRoute) {
      state.nextRoute = nextRoute
    },
    setReturnRoute (state, returnRoute) {
      state.returnRoute = returnRoute
    },
    initializeStore (state) {
      const data = window.localStorage.getItem(STORE_KEY)
      if (data) {
        const json = JSON.parse(data)

        // Récupération de la langue depuis les cookies
        var lang = CookieUtils.getCookie('NG_TRANSLATE_LANG_KEY')
        if (lang !== null && lang !== undefined) {
          lang = lang.replaceAll('"', '')
        }
        if (!json.language) {
          json.language = {}
        }
        json.language.lang = lang || 'fr'
        this.replaceState(Object.assign(state, json))
      }
    }
  },
  getters: {
    getLoginModalOpened: (state) => {
      return state.loginModalOpened
    },
    getPreviousRoute: (state) => {
      return state.previousRoute
    },
    getNextRoute: (state) => {
      return state.nextRoute
    },
    getReturnRoute: (state) => {
      return state.returnRoute
    }
  },
  modules: {
    language,
    principal
  }
})

store.subscribe((mutation, state) => {
  // Mise à jour de la langue dans les cookies
  var lang = null
  if (state.language !== null && state.language !== undefined) {
    lang = state.language.lang
  }
  if (lang !== null && lang !== undefined) {
    // Maj de l'attribut lang de la page
    document.querySelector('html').setAttribute('lang', lang)
    lang = '"' + lang + '"'
  }
  CookieUtils.setCookie('NG_TRANSLATE_LANG_KEY', lang || '"fr"')

  // Suppression des propriétés à ne pas persister
  const reducer = Object.assign({}, state)
  delete reducer.loginModalOpened

  window.localStorage.setItem(STORE_KEY, JSON.stringify(reducer))
})

export default store
