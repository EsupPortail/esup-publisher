import FetchWrapper from "../../util/FetchWrapper";

class ContextService {
  query(search) {
    return FetchWrapper.getJson(
      "api/contexts?" +
        new URLSearchParams({
          search: search,
        })
    );
  }
}

export default new ContextService();
