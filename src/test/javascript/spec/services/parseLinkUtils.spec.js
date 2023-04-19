import ParseLinkUtils from "@/services/util/ParseLinkUtils.js";

// Tests unitaires sur la classe utilitaire ParseLinkUtils
describe("ParseLinkUtils.js tests", () => {
  it("test 1 Base64Utils - parse", () => {
    const value =
      '</api/items?page=3&per_page=5>; rel="next",</api/items?page=1&per_page=5>; rel="prev"';
    const links = ParseLinkUtils.parse(value);
    expect(links.prev).toStrictEqual(1);
    expect(links.next).toStrictEqual(3);
  });
});
