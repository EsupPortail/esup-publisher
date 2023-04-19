import ConfigurationService from "@/services/admin/ConfigurationService.js";
import FetchWrapper from "@/services/util/FetchWrapper.js";

jest.mock("@/services/util/FetchWrapper.js");

describe("ConfigurationService.js tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("test 1 ConfigurationService - get", (done) => {
    const response = {
      data: {
        key1: "val1",
        key2: "val2",
      },
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    ConfigurationService.get().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(
        "management/configprops"
      );
      expect(value).toStrictEqual(["val1", "val2"]);
      done();
    });
  });

  it("test 2 ConfigurationService - getEnv", (done) => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    ConfigurationService.getEnv().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith("management/env");
      expect(value).toStrictEqual(response);
      done();
    });
  });
});
