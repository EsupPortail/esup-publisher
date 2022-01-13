import FetchWrapper from '../../util/FetchWrapper'
import DateUtils from '../../util/DateUtils'

class ItemService {
  query (params) {
    if (params) {
      return FetchWrapper.getJson('api/items?' + new URLSearchParams(params))
    } else {
      return FetchWrapper.getJson('api/items')
    }
  }

  get (id) {
    return FetchWrapper.getJson('api/items/' + id).then(data => {
      if (data) {
        data.endDate = DateUtils.convertLocalDateFromServer(data.endDate)
        data.startDate = DateUtils.convertLocalDateFromServer(data.startDate)
        data.validatedDate = DateUtils.convertDateTimeFromServer(data.validatedDate)
        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate)
        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate)
      }
      return data
    })
  }

  update (item) {
    const copy = Object.assign({}, item)
    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate)
    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate)
    return FetchWrapper.putJson('api/items', copy)
  }

  save (item) {
    const copy = Object.assign({}, item)
    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate)
    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate)
    return FetchWrapper.postJson('api/items', copy)
  }

  patch (item) {
    return FetchWrapper.putJson('api/items/action', item)
  }

  delete (id) {
    return FetchWrapper.deleteJson('api/items/' + id)
  }
}

export default new ItemService()
