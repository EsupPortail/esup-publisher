import FetchWrapper from '../util/FetchWrapper'

class ConfMimeTypesService {
  query () {
    return FetchWrapper.getJson('api/conf/authorizedmimetypes')
  }
}

export default new ConfMimeTypesService()
