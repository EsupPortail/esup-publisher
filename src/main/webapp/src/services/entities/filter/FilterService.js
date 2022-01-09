import FetchWrapper from '../../util/FetchWrapper'

class FilterService {
  query () {
    return FetchWrapper.getJson('api/filters')
  }

  get (id) {
    return FetchWrapper.getJson('api/filters/' + id)
  }

  update (filter) {
    return FetchWrapper.putJson('api/filters', filter)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/filters/' + id)
  }
}

export default new FilterService()
