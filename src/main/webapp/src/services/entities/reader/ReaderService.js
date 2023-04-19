import FetchWrapper from "../../util/FetchWrapper";

class ReaderService {
  query() {
    return FetchWrapper.getJson("api/readers");
  }

  get(id) {
    return FetchWrapper.getJson("api/readers/" + id);
  }

  update(reader) {
    return FetchWrapper.putJson("api/readers", reader);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/readers/" + id);
  }
}

export default new ReaderService();
