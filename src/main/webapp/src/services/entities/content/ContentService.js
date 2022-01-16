import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class ContentService {
  query () {
    return FetchWrapper.getJson('api/contents')
  }

  get (id) {
    return FetchWrapper.getJson('api/contents/' + id).then(response => {
      if (response.data) {
        response.data.item.endDate = DateUtils.convertLocalDateFromServer(response.data.item.endDate)
        response.data.item.startDate = DateUtils.convertLocalDateFromServer(response.data.item.startDate)
        response.data.item.validatedDate = DateUtils.convertDateTimeFromServer(response.data.item.validatedDate)
        response.data.item.createdDate = DateUtils.convertDateTimeFromServer(response.data.item.createdDate)
        response.data.item.lastModifiedDate = DateUtils.convertDateTimeFromServer(response.data.item.lastModifiedDate)
      }
      return response
    })
  }

  update (content) {
    const copy = Object.assign({}, content)
    copy.item.startDate = DateUtils.convertLocalDateToServer(copy.item.startDate)
    copy.item.endDate = DateUtils.convertLocalDateToServer(copy.item.endDate)
    return FetchWrapper.putJson('api/contents', copy)
  }

  save (content) {
    const copy = Object.assign({}, content)
    copy.item.startDate = DateUtils.convertLocalDateToServer(copy.item.startDate)
    copy.item.endDate = DateUtils.convertLocalDateToServer(copy.item.endDate)
    return FetchWrapper.postJson('api/contents', copy)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/contents/' + id)
  }
}

export default new ContentService()
