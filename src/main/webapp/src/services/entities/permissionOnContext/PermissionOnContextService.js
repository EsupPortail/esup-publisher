import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class PermissionOnContextService {
  query () {
    return FetchWrapper.getJson('api/permissionOnContexts')
  }

  get (id) {
    return FetchWrapper.getJson('api/permissionOnContexts/' + id).then(response => {
      if (response.data) {
        response.data.createdDate = DateUtils.convertDateTimeFromServer(response.data.createdDate)
        response.data.lastModifiedDate = DateUtils.convertDateTimeFromServer(response.data.lastModifiedDate)
      }
      return response
    })
  }

  update (permissionOnContext) {
    return FetchWrapper.putJson('api/permissionOnContexts', permissionOnContext)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/permissionOnContexts/' + id)
  }

  queryForCtx (ctxType, ctxId) {
    return FetchWrapper.getJson('api/permissionOnContexts/' + ctxType + '/' + ctxId)
  }
}

export default new PermissionOnContextService()
