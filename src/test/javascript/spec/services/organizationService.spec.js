import OrganizationService from "@/services/entities/organization/OrganizationService.js";
import FetchWrapper from "@/services/util/FetchWrapper.js";
import DateUtils from "@/services/util/DateUtils";

jest.mock("@/services/util/FetchWrapper.js");

describe("OrganizationService.js tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("test 1 OrganizationService - query", (done) => {
    const response = {
      data: [],
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    OrganizationService.query().then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith("api/organizations");
      expect(value).toStrictEqual(response);
      done();
    });
  });

  it("test 2 OrganizationService - get", (done) => {
    const response = {
      data: {
        id: 1,
        createdDate: "2022-01-20T10:30:44Z",
        lastModifiedDate: "2022-04-12T11:58:02Z",
      },
      headers: [],
    };
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response));

    const id = 1;
    OrganizationService.get(id).then((value) => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.getJson).toHaveBeenCalledWith(
        "api/organizations/" + id
      );
      expect(value).toStrictEqual({
        data: {
          id: response.data.id,
          createdDate: DateUtils.convertDateTimeFromServer(
            response.data.createdDate
          ),
          lastModifiedDate: DateUtils.convertDateTimeFromServer(
            response.data.lastModifiedDate
          ),
        },
        headers: [],
      });
      done();
    });
  });

  it("test 3 OrganizationService - update", (done) => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve(response));

    const data = {
      id: 1,
    };
    OrganizationService.update(data).then((value) => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.putJson).toHaveBeenCalledWith(
        "api/organizations",
        data
      );
      expect(value).toStrictEqual(response);
      done();
    });
  });

  it("test 4 OrganizationService - delete", (done) => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.deleteJson = jest
      .fn()
      .mockReturnValue(Promise.resolve(response));

    const id = 1;
    OrganizationService.delete(id).then((value) => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith(
        "api/organizations/" + id
      );
      expect(value).toStrictEqual(response);
      done();
    });
  });
});
