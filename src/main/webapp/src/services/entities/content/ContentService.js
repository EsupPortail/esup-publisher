import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class ContentService {
  query () {
    return FetchWrapper.getJson('api/contents')
  }

  get (id) {
    return FetchWrapper.getJson('api/contents/' + id).then(data => {
      if (data) {
        data.item.endDate = DateUtils.convertLocalDateFromServer(data.item.endDate)
        data.item.startDate = DateUtils.convertLocalDateFromServer(data.item.startDate)
        data.item.validatedDate = DateUtils.convertDateTimeFromServer(data.item.validatedDate)
        data.item.createdDate = DateUtils.convertDateTimeFromServer(data.item.createdDate)
        data.item.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.item.lastModifiedDate)
      }
      return data
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
