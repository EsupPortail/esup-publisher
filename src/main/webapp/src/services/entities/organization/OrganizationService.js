import FetchWrapper from "../../util/FetchWrapper";
import DateUtils from "../../util/DateUtils";

class OrganizationService {
  query() {
    return FetchWrapper.getJson("api/organizations");
  }

  get(id) {
    return FetchWrapper.getJson("api/organizations/" + id).then((response) => {
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

  update(organization) {
    return FetchWrapper.putJson("api/organizations", organization);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/organizations/" + id);
  }
}

export default new OrganizationService();
