const state = {
  lang: "fr",
};

const mutations = {
  setLang(_state, payload) {
    _state.lang = payload;
  },
};

const getters = {
  getLanguage: (_state) => {
    return _state.lang;
  },
};

export default {
  state,
  mutations,
  getters,
};
