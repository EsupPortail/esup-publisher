import AuditsService from "@/services/admin/AuditsService.js";
import FetchWrapper from "@/services/util/FetchWrapper.js";

jest.mock("@/services/util/FetchWrapper.js");

describe("AuditsService.js tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("test 1 AuditsService - findAll", (done) => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    AuditsService.findAll().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith("api/audits/all");
      expect(value).toStrictEqual(response);
      done();
    });
  });

  it("test 2 AuditsService - findByDates", (done) => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    const fromDate = new Date(2022, 0, 1, 0, 0, 0);
    const toDate = new Date(2022, 0, 31, 0, 0, 0);
    AuditsService.findByDates(fromDate, toDate).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(
        "api/audits/byDates?fromDate=2022-01-01T00%3A00%3A00.000Z&toDate=2022-01-31T00%3A00%3A00.000Z"
      );
      expect(value).toStrictEqual(response);
      done();
    });
  });
});
