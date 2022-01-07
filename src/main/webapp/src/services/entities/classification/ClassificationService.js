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
    return FetchWrapper.getJson('api/classifications/' + id).then(data => {
      if (data) {
        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate)
        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate)
      }
      return data
    })
  }

  update (classification) {
    return FetchWrapper.putJson('api/classifications', classification)
  }

  highlighted () {
    return FetchWrapper.getJson('api/classifications/highlighted')
  }
}

export default new ClassificationService()
