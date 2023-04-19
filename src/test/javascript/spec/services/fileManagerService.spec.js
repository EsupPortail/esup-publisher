import FileManagerService from "@/services/entities/file/FileManagerService.js";
import FetchWrapper from "@/services/util/FetchWrapper.js";

jest.mock("@/services/util/FetchWrapper.js");

describe("FileManagerService.js tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("test 1 FileManagerService - delete", (done) => {
    const response = {
      data: {},
      headers: [],
    };
    FetchWrapper.deleteJson = jest
      .fn()
      .mockReturnValue(Promise.resolve(response));

    const entityId = 1;
    const isPublic = true;
    const fileUri = "fileUri";
    FileManagerService.delete(entityId, isPublic, fileUri).then((value) => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1);
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith(
        "api/file/" + entityId + "/" + isPublic + "/" + fileUri
      );
      expect(value).toStrictEqual(response);
      done();
    });
  });
});
