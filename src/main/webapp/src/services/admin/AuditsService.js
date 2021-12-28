import FetchWrapper from '../util/FetchWrapper'

class AuditsService {
  findAll () {
    return FetchWrapper.getJson('api/audits/all')
  }

  findByDates (fromDate, toDate) {
    return FetchWrapper.getJson('api/audits/byDates?' + new URLSearchParams({
      fromDate: formatDate(fromDate),
      toDate: formatDate(toDate)
    }))
  }
}

function formatDate (date) {
  return date && new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString()
}

export default new AuditsService()
