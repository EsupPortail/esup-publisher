class FetchWrapper {
  getJson (url) {
    return new Promise((resolve, reject) => {
      fetch(process.env.VUE_APP_BACK_BASE_URL + url)
        .then(response => {
          if (!response.ok) {
            reject(response)
          } else {
            response.text().then(text => {
              resolve(text ? JSON.parse(text) : null)
            }).catch(error => {
              reject(error)
            })
          }
        }).catch(error => {
          reject(error)
        })
    })
  }

  getJsonP (url) {
    return jsonp(process.env.VUE_APP_BACK_BASE_URL + url, 'JSON_CALLBACK', 1000)
  }

  postJson (url, data) {
    return new Promise((resolve, reject) => {
      fetch(process.env.VUE_APP_BACK_BASE_URL + url, { method: 'POST', body: JSON.stringify(data), headers: getHeader() })
        .then(response => {
          if (!response.ok) {
            reject(response)
          } else {
            response.text().then(text => {
              resolve(text ? JSON.parse(text) : null)
            }).catch(error => {
              reject(error)
            })
          }
        }).catch(error => {
          reject(error)
        })
    })
  }

  putJson (url, data) {
    return new Promise((resolve, reject) => {
      fetch(process.env.VUE_APP_BACK_BASE_URL + url, { method: 'PUT', body: JSON.stringify(data), headers: getHeader() })
        .then(response => {
          if (!response.ok) {
            reject(response)
          } else {
            response.text().then(text => {
              resolve(text ? JSON.parse(text) : null)
            }).catch(error => {
              reject(error)
            })
          }
        }).catch(error => {
          reject(error)
        })
    })
  }

  deleteJson (url) {
    return new Promise((resolve, reject) => {
      fetch(process.env.VUE_APP_BACK_BASE_URL + url, { method: 'DELETE', headers: getHeader() })
        .then(response => {
          if (!response.ok) {
            reject(response)
          } else {
            response.text().then(text => {
              resolve(text ? JSON.parse(text) : null)
            }).catch(error => {
              reject(error)
            })
          }
        }).catch(error => {
          reject(error)
        })
    })
  }
}

function getHeader () {
  return new Headers({
    'Content-Type': 'application/json;charset=UTF-8',
    'X-CSRF-TOKEN': getCookie('CSRF-TOKEN')
  })
}

function getCookie (name) {
  if (!document.cookie) {
    return null
  }

  const xsrfCookies = document.cookie.split(';')
    .map(c => c.trim())
    .filter(c => c.startsWith(name + '='))

  if (xsrfCookies.length === 0) {
    return null
  }
  return decodeURIComponent(xsrfCookies[0].split('=')[1])
}

function jsonp (url, callbackName, timeout) {
  return new Promise((resolve, reject) => {
    callbackName = typeof callbackName === 'string' ? callbackName : 'jsonp_' + (Math.floor(Math.random() * 100000) * Date.now()).toString(16)
    timeout = typeof timeout === 'number' ? timeout : 5000

    let timeoutTimer = null
    if (timeout > -1) {
      timeoutTimer = setTimeout(() => {
        console.log('Timeout JSONP')
        removeErrorListener()
        removeScript()
        reject(new Error({
          statusText: 'Request Timeout',
          status: 408
        }))
      }, timeout)
    }

    const onError = (err) => {
      console.log('Error JSONP', err)
      if (timeoutTimer) {
        clearTimeout(timeoutTimer)
      }
      removeErrorListener()
      reject(new Error({
        status: 400,
        statusText: 'Bad Request'
      }))
    }

    window[callbackName] = (json) => {
      if (timeoutTimer) {
        clearTimeout(timeoutTimer)
      }
      removeErrorListener()
      removeScript()
      resolve(json)
    }

    const script = document.createElement('script')
    script.type = 'text/javascript'

    const removeErrorListener = () => {
      script.removeEventListener('error', onError)
    }
    const removeScript = () => {
      document.body.removeChild(script)
      delete window[callbackName]
    }

    // Add error listener.
    script.addEventListener('error', onError)

    // Append to head element.
    script.src = url + (/\?/.test(url) ? '&' : '?') + 'callback=' + callbackName
    script.async = true
    document.body.appendChild(script)
  })
}

export default new FetchWrapper()
