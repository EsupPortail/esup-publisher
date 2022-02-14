import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class ClassificationService {
  query (publisherId, isPublishing) {
    return FetchWrapper.getJson('api/classifications?' + new URLSearchParams({
      publisherId: publisherId,
      isPublishing: isPublishing
    }))
  }

  get (id) {
    return FetchWrapper.getJson('api/classifications/' + id).then(response => {
      if (response.data) {
        response.data.createdDate = DateUtils.convertDateTimeFromServer(response.data.createdDate)
        response.data.lastModifiedDate = DateUtils.convertDateTimeFromServer(response.data.lastModifiedDate)
      }
      return response
    })
  }

  update (classification) {
    return FetchWrapper.putJson('api/classifications', classification)
  }

  highlighted () {
    return FetchWrapper.getJson('api/classifications/highlighted')
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/classifications/' + id)
  }
}

export default new ClassificationService()
