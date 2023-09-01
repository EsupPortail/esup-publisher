import FetchWrapper from '../../util/FetchWrapper';
import DateUtils from '../../util/DateUtils';

class ItemService {
  query(params) {
    if (params) {
      return FetchWrapper.getJson('api/items?' + new URLSearchParams(params));
    } else {
      return FetchWrapper.getJson('api/items');
    }
  }

  get(id) {
    return FetchWrapper.getJson('api/items/' + id).then((response) => {
      if (response.data) {
        response.data.endDate = DateUtils.convertLocalDateFromServer(response.data.endDate);
        response.data.startDate = DateUtils.convertLocalDateFromServer(response.data.startDate);
        response.data.validatedDate = DateUtils.convertDateTimeFromServer(response.data.validatedDate);
        response.data.createdDate = DateUtils.convertDateTimeFromServer(response.data.createdDate);
        response.data.lastModifiedDate = DateUtils.convertDateTimeFromServer(response.data.lastModifiedDate);
      }
      return response;
    });
  }

  update(item) {
    const copy = Object.assign({}, item);
    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
    return FetchWrapper.putJson('api/items', copy);
  }

  save(item) {
    const copy = Object.assign({}, item);
    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
    return FetchWrapper.postJson('api/items', copy);
  }

  patch(item) {
    return FetchWrapper.putJson('api/items/action', item);
  }

  delete(id) {
    return FetchWrapper.deleteJson('api/items/' + id);
  }
}

export default new ItemService();
