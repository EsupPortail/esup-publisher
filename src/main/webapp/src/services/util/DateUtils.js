import CommonUtils from './CommonUtils'

const toString = Object.prototype.toString

class DateUtils {
  convertDateTimeFromServer (date) {
    if (date) {
      return new Date(date)
    } else {
      return null
    }
  }

  convertLocalDateFromServer (date) {
    if (date) {
      var dateString = date.split('-')
      return new Date(dateString[0], dateString[1] - 1, dateString[2])
    }
    return null
  }

  convertLocalDateToServer (date) {
    if (date) {
      var parseDate = new Date(date)
      var day = parseDate.getDate().toString().length === 1 ? '0' + parseDate.getDate() : parseDate.getDate()
      var month = parseDate.getMonth().toString().length === 1 ? '0' + (parseDate.getMonth() + 1) : (parseDate.getMonth() + 1)
      return parseDate.getFullYear() + '-' + month + '-' + day
    } else {
      return null
    }
  }

  addDaysToLocalDate (date, nbDays) {
    if (date && toString.call(date) === '[object Date]' && !isNaN(nbDays) && typeof nbDays === 'number') {
      var tmp = new Date(date.getTime() + nbDays * 24 * 60 * 60 * 1000)
      return new Date(new Date(tmp.getFullYear(), tmp.getMonth(), tmp.getDate()))
    }
    return null
  }

  dateformat () {
    return 'yyyy-MM-dd'
  }

  convertToIntString (date, format, lang) {
    if (date && format && lang) {
      return Intl.DateTimeFormat(lang, format).format(CommonUtils.isString(date) ? new Date(date) : date)
    } else {
      return null
    }
  }

  formatDateToShortIntString (date, lang) {
    if (date && lang) {
      return this.convertToIntString(date, {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      }, lang)
    } else {
      return null
    }
  }

  formatDateToLongIntString (date, lang) {
    if (date && lang) {
      return this.convertToIntString(date, {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      }, lang)
    } else {
      return null
    }
  }

  formatDateTimeToShortIntString (date, lang) {
    if (date && lang) {
      return this.convertToIntString(date, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
      }, lang)
    } else {
      return null
    }
  }

  formatDateTimeToLongIntString (date, lang) {
    if (date && lang) {
      return this.convertToIntString(date, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
      }, lang)
    } else {
      return null
    }
  }

  isValidDate (dateStr) {
    if (dateStr === undefined) {
      return false
    }
    var dateTime = new Date(dateStr)

    if (isNaN(dateTime)) {
      return false
    }
    return true
  }

  getDateDifference (fromDate, toDate) {
    return new Date(toDate) - new Date(fromDate)
  }

  isValidDateRange (fromDate, toDate) {
    if (fromDate === '' || toDate === '') {
      return true
    }
    if (this.isValidDate(fromDate) === false) {
      return false
    }
    if (this.isValidDate(toDate) === true) {
      var days = this.getDateDifference(fromDate, toDate)
      if (days <= 0) {
        return false
      }
    }
    return true
  }

  // normalize string dd/mm/yyyy or yyyy-mm-dd into date
  normalize (str) {
    if (str && !CommonUtils.isDate(str)) {
      var match = str.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/)
      if (match) {
        return new Date(match[3], match[2] - 1, match[1])
      }
      match = str.match(/^(\d{4})-(\d{1,2})-(\d{1,2})$/)
      if (match) {
        return new Date(match[1], match[2] - 1, match[3])
      }
    }
  }
}
export default new DateUtils()
