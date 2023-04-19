import FetchWrapper from "../../util/FetchWrapper";
import DateUtils from "../../util/DateUtils";

class PublisherService {
  query(params) {
    if (params) {
      return FetchWrapper.getJson(
        "api/publishers?" + new URLSearchParams(params)
      );
    } else {
      return FetchWrapper.getJson("api/publishers");
    }
  }

  get(id) {
    return FetchWrapper.getJson("api/publishers/" + id).then((response) => {
      if (response.data) {
        response.data.createdDate = DateUtils.convertDateTimeFromServer(
          response.data.createdDate
        );
        response.data.lastModifiedDate = DateUtils.convertDateTimeFromServer(
          response.data.lastModifiedDate
        );
      }
      return response;
    });
  }

  update(publisher) {
    return FetchWrapper.putJson("api/publishers", publisher);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/publishers/" + id);
  }
}

export default new PublisherService();
