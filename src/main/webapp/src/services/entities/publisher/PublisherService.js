import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class PublisherService {
  query () {
    return FetchWrapper.getJson('api/publishers')
  }

  get (id) {
    return FetchWrapper.getJson('api/publishers/' + id).then(data => {
      if (data) {
        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate)
        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate)
      }
      return data
    })
  }

  update (publisher) {
    return FetchWrapper.putJson('api/publishers', publisher)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/publishers/' + id)
  }
}

export default new PublisherService()
