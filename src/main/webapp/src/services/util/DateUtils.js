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
}
export default new DateUtils()
