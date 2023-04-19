import MonitoringService from "@/services/admin/MonitoringService.js";
import FetchWrapper from "@/services/util/FetchWrapper.js";

jest.mock("@/services/util/FetchWrapper.js");

describe("MonitoringService.js tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("test 1 MonitoringService - getMetrics", (done) => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    MonitoringService.getMetrics().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(
        "management/jhimetrics"
      );
      expect(value).toStrictEqual(response);
      done();
    });
  });

  it("test 2 MonitoringService - checkHealth", (done) => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    MonitoringService.checkHealth().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith("management/health");
      expect(value).toStrictEqual(response);
      done();
    });
  });

  it("test 3 MonitoringService - threadDump", (done) => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    MonitoringService.threadDump().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(
        "management/threaddump"
      );
      expect(value).toStrictEqual(response);
      done();
    });
  });
});
