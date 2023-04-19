import FetchWrapper from "../util/FetchWrapper";

class ConfFileSizeService {
  query() {
    return FetchWrapper.getJson("api/conf/uploadfilesize");
  }
}

export default new ConfFileSizeService();
