class Base64Utils {
  encode (input) {
    return window.btoa(input)
  }

  decode (input) {
    return window.atob(input)
  }
}

export default new Base64Utils()
