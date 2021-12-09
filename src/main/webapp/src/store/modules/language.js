const state = {
  lang: 'fr'
}

const mutations = {
  setLang (state, payload) {
    state.lang = payload
  }
}

const getters = {
  getLanguage: (state) => {
    return state.lang
  }
}

export default {
  state,
  mutations,
  getters
}
