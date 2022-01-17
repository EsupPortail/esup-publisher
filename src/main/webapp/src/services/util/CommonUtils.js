// Classe contenant des méthodes utilitaires provenant majoritairement d'AngularJS
// (https://github.com/angular/angular.js/blob/master/src/Angular.js)
class CommonUtils {
  isString (value) {
    return typeof value === 'string'
  }

  isArray (arr) {
    return Array.isArray(arr) || arr instanceof Array
  }

  isDate (value) {
    return toString.call(value) === '[object Date]'
  }

  isRegExp (value) {
    return toString.call(value) === '[object RegExp]'
  }

  isWindow (obj) {
    return obj && obj.window === obj
  }

  isScope (obj) {
    return obj && obj.$evalAsync && obj.$watch
  }

  isFunction (value) {
    return typeof value === 'function'
  }

  isDefined (value) {
    return typeof value !== 'undefined'
  }

  isObject (value) {
    return value !== null && typeof value === 'object'
  }

  isNumber (value) {
    return typeof value === 'number'
  }

  simpleCompare (a, b) {
    // eslint-disable-next-line no-self-compare
    return a === b || (a !== a && b !== b)
  }

  equals (o1, o2) {
    if (o1 === o2) return true
    if (o1 === null || o2 === null) return false
    // eslint-disable-next-line no-self-compare
    if (o1 !== o1 && o2 !== o2) return true // NaN === NaN
    var t1 = typeof o1
    var t2 = typeof o2
    var length, key, keySet
    if (t1 === t2 && t1 === 'object') {
      if (this.isArray(o1)) {
        if (!this.isArray(o2)) return false
        if ((length = o1.length) === o2.length) {
          for (key = 0; key < length; key++) {
            if (!this.equals(o1[key], o2[key])) return false
          }
          return true
        }
      } else if (this.isDate(o1)) {
        if (!this.isDate(o2)) return false
        return this.simpleCompare(o1.getTime(), o2.getTime())
      } else if (this.isRegExp(o1)) {
        if (!this.isRegExp(o2)) return false
        return o1.toString() === o2.toString()
      } else {
        if (this.isScope(o1) || this.isScope(o2) || this.isWindow(o1) || this.isWindow(o2) ||
        this.isArray(o2) || this.isDate(o2) || this.isRegExp(o2)) return false
        keySet = Object.create(null)
        for (key in o1) {
          if (key.charAt(0) === '$' || this.isFunction(o1[key])) continue
          if (!this.equals(o1[key], o2[key])) return false
          keySet[key] = true
        }
        for (key in o2) {
          if (!(key in keySet) &&
            key.charAt(0) !== '$' &&
            this.isDefined(o2[key]) &&
            !this.isFunction(o2[key])) return false
        }
        return true
      }
    }
    return false
  }
}

export default new CommonUtils()
