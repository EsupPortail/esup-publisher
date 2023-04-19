import CommonUtils from "@/services/util/CommonUtils.js";

// Tests unitaires sur la classe utilitaire CommonUtils
describe("CommonUtils.js tests", () => {
  it("test 1 CommonUtils - isString function OK", () => {
    var value = "testString";
    expect(CommonUtils.isString(value)).toBe(true);
  });

  it("test 2 CommonUtils - isArray function OK", () => {
    var value = new Array(2);
    expect(CommonUtils.isArray(value)).toBe(true);
  });

  it("test 3 CommonUtils - isRegExp function OK", () => {
    var value = /a+b+c/;
    expect(CommonUtils.isRegExp(value)).toBe(true);
  });

  it("test 4 CommonUtils - isDate function OK", () => {
    var value = new Date("2021", "11", "09");
    expect(CommonUtils.isDate(value)).toBe(true);
  });

  it("test 5 CommonUtils - isWindow function OK", () => {
    expect(CommonUtils.isWindow(window)).toBe(true);
  });

  it("test 6 CommonUtils - isFunction function OK", () => {
    expect(
      CommonUtils.isFunction(function () {
        return 0;
      })
    ).toBe(true);
  });

  it("test 7 CommonUtils - isDefined function OK", () => {
    var value = "testString";
    expect(CommonUtils.isDefined(value)).toBe(true);
  });

  it("test 8 CommonUtils - isObject function OK", () => {
    var object = {
      test: "test",
    };
    expect(CommonUtils.isObject(object)).toBe(true);
  });

  it("test 9 CommonUtils - isNumber function OK", () => {
    var value = 2;
    expect(CommonUtils.isNumber(value)).toBe(true);
  });

  it("test 10 CommonUtils - isFile function OK", () => {
    var blob = new Blob(["test"], { type: "text/plain;charset=utf-8" });
    expect(CommonUtils.isFile(blob)).toBe(true);
  });

  it("test 11 CommonUtils - isNumber function OK", () => {
    var value = 2;
    expect(CommonUtils.isNumber(value)).toBe(true);
  });

  it("test 12 CommonUtils - simpleCompare function OK", () => {
    var a = {
      nom: "test",
    };
    var b = a;
    var c = {
      nom: "testdifferent",
    };
    expect(CommonUtils.simpleCompare(a, b)).toBe(true);
    expect(CommonUtils.simpleCompare(a, c)).toBe(false);
  });

  it("test 13 CommonUtils - equals function OK", () => {
    var obj1 = {
      test: "test",
    };
    var obj2 = {
      test: "test",
    };
    var obj3 = new Array(2);
    var obj4 = new Date("2021", "11", "09");
    expect(CommonUtils.equals(obj1, obj2)).toBe(true);
    expect(CommonUtils.equals(obj1, obj3)).toBe(false);
    expect(CommonUtils.equals(obj1, obj4)).toBe(false);
    expect(CommonUtils.equals(obj3, obj4)).toBe(false);
  });

  it("test 14 CommonUtils - convertToDecimal function OK", () => {
    var num = 15.26648;
    var round = 15.27;
    var decimal = 2;
    expect(CommonUtils.convertToDecimal(num, decimal)).toStrictEqual(round);
  });

  it("test 15 CommonUtils - convertByteToDisplayedString function OK", () => {
    var expectedValue = "195.31 KB";
    expect(CommonUtils.convertByteToDisplayedString(200000, 2)).toStrictEqual(
      expectedValue
    );
  });

  it("test 16 CommonUtils - removeHtmlTags function OK", () => {
    const htmlString = "<p>Test</p>";
    const expectString = "Test";
    expect(CommonUtils.removeHtmlTags(htmlString)).toStrictEqual(expectString);
  });
});
