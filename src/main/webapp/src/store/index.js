import { createStore } from 'vuex'
import language from './modules/language'
import principal from './modules/principal'
import createPersistedState from 'vuex-persistedstate'
import CookieUtils from '@/services/util/CookieUtils'

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
  actions: {
  },
  modules: {
    language,
    principal
  },
  plugins: [createPersistedState({
    storage: {
      getItem: (key) => {
        // Récupération de la langue depuis les cookies
        if (key === 'vuex') {
          var lang = CookieUtils.getCookie('NG_TRANSLATE_LANG_KEY')
          if (lang !== null && lang !== undefined) {
            lang = lang.replaceAll('"', '')
          }
          const item = window.localStorage.getItem(key)
          if (item !== null && item !== undefined) {
            const json = JSON.parse(item)
            if (!json.language) {
              json.language = {}
            }
            json.language.lang = lang || 'fr'
            return JSON.stringify(json)
          } else {
            return item
          }
        } else {
          return window.localStorage.getItem(key)
        }
      },
      setItem: (key, value) => {
        // Mise à jour de la langue dans les cookies
        if (key === 'vuex') {
          var lang = null
          if (value !== null && value !== undefined) {
            const json = JSON.parse(value)
            if (json.language !== null && json.language !== undefined) {
              lang = json.language.lang
            }
          }
          if (lang !== null && lang !== undefined) {
            // Maj de l'attribut lang de la page
            document.querySelector('html').setAttribute('lang', lang)
            lang = '"' + lang + '"'
          }
          CookieUtils.setCookie('NG_TRANSLATE_LANG_KEY', lang || '"fr"')
        }
        window.localStorage.setItem(key, value)
      },
      removeItem: (key) => {
        window.localStorage.removeItem(key)
      }
    },
    reducer: (state) => {
      // Suppression des propriétés à ne pas persister
      const reducer = Object.assign({}, state)
      delete reducer.loginModalOpened

      return (reducer)
    }
  })]
})

export default store
