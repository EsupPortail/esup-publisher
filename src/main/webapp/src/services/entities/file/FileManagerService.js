import FetchWrapper from "../../util/FetchWrapper";

class FileManagerService {
  delete(entityId, isPublic, fileUri) {
    return FetchWrapper.deleteJson(
      "api/file/" + entityId + "/" + isPublic + "/" + fileUri
    );
  }
}

export default new FileManagerService();
