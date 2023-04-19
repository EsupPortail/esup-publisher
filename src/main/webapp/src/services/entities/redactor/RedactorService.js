import FetchWrapper from "../../util/FetchWrapper";

class RedactorService {
  query() {
    return FetchWrapper.getJson("api/redactors");
  }

  get(id) {
    return FetchWrapper.getJson("api/redactors/" + id);
  }

  update(redactor) {
    return FetchWrapper.putJson("api/redactors", redactor);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/redactors/" + id);
  }
}

export default new RedactorService();
