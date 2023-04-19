import FetchWrapper from "../util/FetchWrapper";

class ConfImageSizeService {
  query() {
    return FetchWrapper.getJson("api/conf/uploadimagesize");
  }
}

export default new ConfImageSizeService();
