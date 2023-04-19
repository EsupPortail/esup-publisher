import FetchWrapper from "../../util/FetchWrapper";
import DateUtils from "../../util/DateUtils";

class CategoryService {
  query() {
    return FetchWrapper.getJson("api/categorys");
  }

  get(id) {
    return FetchWrapper.getJson("api/categorys/" + id).then((response) => {
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

  update(category) {
    return FetchWrapper.putJson("api/categorys", category);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/categorys/" + id);
  }
}

export default new CategoryService();
