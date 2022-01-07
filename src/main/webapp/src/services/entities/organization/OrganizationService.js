import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class OrganizationService {
  query () {
    return FetchWrapper.getJson('api/organizations')
  }

  get (id) {
    return FetchWrapper.getJson('api/organizations/' + id).then(data => {
      if (data) {
        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate)
        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate)
      }
      return data
    })
  }

  update (organization) {
    return FetchWrapper.putJson('api/organizations', organization)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/organizations/' + id)
  }
}

export default new OrganizationService()
