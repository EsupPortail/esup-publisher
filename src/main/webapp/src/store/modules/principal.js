const state = {
  _identity: null,
  _authenticated: false,
};

const mutations = {
  setIdentity(_state, identity) {
    _state._identity = identity;
  },
  setAuthenticated(_state, authenticated) {
    _state._authenticated = authenticated;
  },
  clearAll(_state) {
    _state._identity = null;
    _state._authenticated = false;
  },
};
const getters = {
  getIdentity: (_state) => {
    return _state._identity;
  },
  getAuthenticated: (_state) => {
    return _state._authenticated;
  },
};

export default {
  state,
  mutations,
  getters,
};
