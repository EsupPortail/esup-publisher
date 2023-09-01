import router from '@/router/index.js';
import store from '@/store/index.js';
import { ref } from 'vue';
import CookieUtils from './CookieUtils';

class FetchWrapper {
  countPendingRequests() {
    return countPendingRequests;
  }

  getJson(url) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(resolve, reject, process.env.VUE_APP_BACK_BASE_URL + url);
    });
  }

  getJsonP(url) {
    return jsonp(process.env.VUE_APP_BACK_BASE_URL + url, 'JSON_CALLBACK', 1000);
  }

  postJson(url, data) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(resolve, reject, process.env.VUE_APP_BACK_BASE_URL + url, {
        method: 'POST',
        body: JSON.stringify(data),
        headers: getHeader(),
      });
    });
  }

  putJson(url, data) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(resolve, reject, process.env.VUE_APP_BACK_BASE_URL + url, {
        method: 'PUT',
        body: JSON.stringify(data),
        headers: getHeader(),
      });
    });
  }

  deleteJson(url) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(resolve, reject, process.env.VUE_APP_BACK_BASE_URL + url, {
        method: 'DELETE',
        headers: getHeader(),
      });
    });
  }
}

// Compteur des requêtes en cours
var countPendingRequests = ref(0);

// Fonction permettant de faire une requête JSON et de la réessayer après login si erreur 401
function fetchWithRetry(resolve, reject, url, params) {
  countPendingRequests.value++;
  fetch(url, params)
    .then((response) => {
      if (!response.ok) {
        if (response.status !== 401 || url === process.env.VUE_APP_BACK_BASE_URL + 'api/account' || store.getters.getLoginModalOpened) {
          reject(response);
        } else {
          // Si erreur 401, redirection vers la page de login
          if (router.currentRoute.value.name !== 'Login') {
            store.commit('setLoginModalOpened', true);
            store.commit('setReturnRoute', {
              name: router.currentRoute.value.name,
              params: router.currentRoute.value.params,
              meta: router.currentRoute.value.meta,
            });
            router.push({ name: 'Login' });
          }
        }
      } else {
        response
          .text()
          .then((text) => {
            resolve({
              data: text ? JSON.parse(text) : null,
              headers: response.headers,
            });
          })
          .catch((error) => {
            reject(error);
          });
      }
    })
    .catch((error) => {
      reject(error);
    })
    .finally(() => {
      countPendingRequests.value--;
    });
}

// Fonction retournant le header des requêtes
function getHeader() {
  return new Headers({
    'Content-Type': 'application/json;charset=UTF-8',
    'X-CSRF-TOKEN': CookieUtils.getCookie('CSRF-TOKEN'),
  });
}

// Fonction permettant d'exécutant une requête de type JSONP
function jsonp(url, callbackName, timeout) {
  return new Promise((resolve, reject) => {
    callbackName =
      typeof callbackName === 'string' ? callbackName : 'jsonp_' + (Math.floor(Math.random() * 100000) * Date.now()).toString(16);
    timeout = typeof timeout === 'number' ? timeout : 5000;

    let timeoutTimer = null;
    if (timeout > -1) {
      timeoutTimer = setTimeout(() => {
        // eslint-disable-next-line
        console.warn('Timeout JSONP');
        removeErrorListener();
        removeScript();
        reject(
          new Error({
            statusText: 'Request Timeout',
            status: 408,
          }),
        );
      }, timeout);
    }

    const onError = (err) => {
      // eslint-disable-next-line
      console.error('Error JSONP', err);
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      reject(
        new Error({
          status: 400,
          statusText: 'Bad Request',
        }),
      );
    };

    window[callbackName] = (json) => {
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      removeScript();
      resolve(json);
    };

    const script = document.createElement('script');
    script.type = 'text/javascript';

    const removeErrorListener = () => {
      script.removeEventListener('error', onError);
    };
    const removeScript = () => {
      document.body.removeChild(script);
      delete window[callbackName];
    };

    // Add error listener.
    script.addEventListener('error', onError);

    // Append to head element.
    script.src = url + (/\?/.test(url) ? '&' : '?') + 'callback=' + callbackName;
    script.async = true;
    document.body.appendChild(script);
  });
}

export default new FetchWrapper();
