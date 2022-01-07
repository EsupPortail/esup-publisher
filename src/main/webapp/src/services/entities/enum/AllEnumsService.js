import FetchWrapper from '../../util/FetchWrapper'

class AllEnumsService {
  query () {
    return FetchWrapper.getJson('api/enums/all')
  }
}

export default new AllEnumsService()
