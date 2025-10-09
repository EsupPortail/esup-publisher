import FetchWrapper from '../util/FetchWrapper.js';

class ConfInjectedWebComponentsService {
  query() {
    return FetchWrapper.getJson('api/conf/injectedWebComponents');
  }
}

export default new ConfInjectedWebComponentsService();
