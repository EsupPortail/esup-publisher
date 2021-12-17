import FetchWrapper from '../../util/FetchWrapper'

class OrganizationService {
  get () {
    return FetchWrapper.getJson('api/organizations')
  }

  getById (id) {
    return FetchWrapper.getJson('api/organizations/' + id)
  }

  update (organization) {
    return FetchWrapper.putJson('api/organizations', organization)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/organizations/' + id)
  }
}

export default new OrganizationService()
