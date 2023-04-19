import FetchWrapper from "../../util/FetchWrapper";
import DateUtils from "../../util/DateUtils";

class PermissionService {
  query() {
    return FetchWrapper.getJson("api/permissions");
  }

  get(id) {
    return FetchWrapper.getJson("api/permissions/" + id).then((response) => {
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

  update(permissionOnContext) {
    return FetchWrapper.putJson("api/permissions", permissionOnContext);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/permissions/" + id);
  }

  queryForCtx(ctxType, ctxId) {
    return FetchWrapper.getJson("api/permissions/" + ctxType + "/" + ctxId);
  }
}

export default new PermissionService();
