import FetchWrapper from '../util/FetchWrapper.js';

class ConfInjectedService {
  query() {
    return FetchWrapper.getJson('api/conf/injected');
  }
}

export default new ConfInjectedService();
