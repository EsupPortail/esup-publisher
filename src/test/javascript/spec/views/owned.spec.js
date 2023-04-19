import { shallowMount, RouterLinkStub } from "@vue/test-utils";
import Owned from "@/views/manager/contents/owned/Owned.vue";
import ClassificationService from "@/services/entities/classification/ClassificationService";
import ContentService from "@/services/entities/content/ContentService";
import EnumDatasService from "@/services/entities/enum/EnumDatasService.js";
import ItemService from "@/services/entities/item/ItemService";
import ParseLinkUtils from "@/services/util/ParseLinkUtils";
import DateUtils from "@/services/util/DateUtils";

jest.mock("@/services/entities/enum/EnumDatasService.js");

// Tests unitaires sur la page Owned
describe("Owned.vue tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it("test 1 Owned - Affichage d'un élément dans la liste des publications", (done) => {
    const item1 = {
      type: "ATTACHMENT",
      id: 39,
      createdBy: { displayName: "Administrator" },
      createdDate: "2022-01-28T16:04:50Z",
      lastModifiedBy: { displayName: "Administrator" },
      lastModifiedDate: "2022-01-28T16:04:50Z",
      title: "PREMIER TEST VIABLE",
      enclosure: null,
      endDate: "2022-01-29",
      startDate: "2022-01-28",
      validatedBy: { displayName: "Administrator" },
      validatedDate: "2022-01-29",
      status: "DRAFT",
      summary: "DESCRIPTION DU PREMIER TEST VIABLE",
      rssAllowed: false,
      highlight: false,
      organization: { name: "Collège De L'Iroise" },
      redactor: { displayName: "Administrator" },
    };
    ItemService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => "link",
        },
      })
    );
    ClassificationService.highlighted = jest.fn().mockReturnValue(
      Promise.resolve({
        data: { name: "À la une" },
      })
    );
    EnumDatasService.getItemStatusList = jest.fn().mockReturnValue([
      { id: 0, name: "PENDING", label: "enum.itemStatus.pending.title" },
      { id: 4, name: "DRAFT", label: "enum.itemStatus.draft.title" },
    ]);
    ParseLinkUtils.parse = jest.fn().mockReturnValue({ last: 1, first: 1 });
    DateUtils.convertToIntString = jest
      .fn()
      .mockReturnValue("1 janv. 2022, 12:00:00");

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const organizations = [{ name: "College" }, { name: "Lycée" }];

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        provide: {
          organizations() {
            return organizations;
          },
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(wrapper.vm.items.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(1);
      });
      expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1);
      expect(ItemService.query).toHaveBeenCalledTimes(1);
      expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1);
      done();
    });
  });

  it("test 2 Owned - Affichage de deux éléments dans la liste des publications", (done) => {
    const item1 = {
      type: "ATTACHMENT",
      id: 39,
      createdBy: { displayName: "Administrator" },
      createdDate: "2022-01-28T16:04:50Z",
      lastModifiedBy: { displayName: "Administrator" },
      lastModifiedDate: "2022-01-28T16:04:50Z",
      title: "PREMIER TEST VIABLE",
      enclosure: null,
      endDate: "2022-01-29",
      startDate: "2022-01-28",
      validatedBy: { displayName: "Administrator" },
      validatedDate: "2022-01-29",
      status: "DRAFT",
      summary: "DESCRIPTION DU PREMIER TEST VIABLE",
      rssAllowed: false,
      highlight: false,
      organization: { name: "Collège De L'Iroise" },
      redactor: { displayName: "Administrator" },
    };
    const item2 = {
      type: "ATTACHMENT",
      id: 39,
      createdBy: { displayName: "Administrator" },
      createdDate: "2022-01-28T16:04:50Z",
      lastModifiedBy: { displayName: "Administrator" },
      lastModifiedDate: "2022-01-28T16:04:50Z",
      title: "PREMIER TEST VIABLE 2",
      enclosure: null,
      endDate: "2022-01-29",
      startDate: "2022-01-28",
      validatedBy: { displayName: "Administrator" },
      validatedDate: "2022-01-29",
      status: "DRAFT",
      summary: "DESCRIPTION DU SECOND TEST VIABLE",
      rssAllowed: false,
      highlight: false,
      organization: { name: "Collège De L'Iroise" },
      redactor: { displayName: "Administrator" },
    };
    ItemService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1, item2]),
        headers: {
          get: () => "link",
        },
      })
    );
    ClassificationService.highlighted = jest.fn().mockReturnValue(
      Promise.resolve({
        data: { name: "À la une" },
      })
    );
    EnumDatasService.getItemStatusList = jest.fn().mockReturnValue([
      { id: 0, name: "PENDING", label: "enum.itemStatus.pending.title" },
      { id: 4, name: "DRAFT", label: "enum.itemStatus.draft.title" },
    ]);
    ParseLinkUtils.parse = jest.fn().mockReturnValue({ last: 1, first: 1 });
    DateUtils.convertToIntString = jest
      .fn()
      .mockReturnValue("1 janv. 2022, 12:00:00");

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const organizations = [{ name: "College" }, { name: "Lycée" }];

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        provide: {
          organizations() {
            return organizations;
          },
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(wrapper.vm.items.length).toStrictEqual(2);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(2);
      });
      expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1);
      expect(ItemService.query).toHaveBeenCalledTimes(1);
      expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1);
      done();
    });
  });

  it("test 3 Owned - itemDetail", (done) => {
    const item1 = {
      type: "ATTACHMENT",
      id: 39,
      createdBy: { displayName: "Administrator" },
      createdDate: "2022-01-28T16:04:50Z",
      lastModifiedBy: { displayName: "Administrator" },
      lastModifiedDate: "2022-01-28T16:04:50Z",
      title: "PREMIER TEST VIABLE",
      enclosure: null,
      endDate: "2022-01-29",
      startDate: "2022-01-28",
      validatedBy: { displayName: "Administrator" },
      validatedDate: "2022-01-29",
      status: "DRAFT",
      summary: "DESCRIPTION DU PREMIER TEST VIABLE",
      rssAllowed: false,
      highlight: false,
      organization: { name: "Collège De L'Iroise" },
      redactor: { displayName: "Administrator" },
    };
    ItemService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => "link",
        },
      })
    );
    ClassificationService.highlighted = jest.fn().mockReturnValue(
      Promise.resolve({
        data: { name: "À la une" },
      })
    );
    EnumDatasService.getItemStatusList = jest.fn().mockReturnValue([
      { id: 0, name: "PENDING", label: "enum.itemStatus.pending.title" },
      { id: 4, name: "DRAFT", label: "enum.itemStatus.draft.title" },
    ]);
    ParseLinkUtils.parse = jest.fn().mockReturnValue({ last: 1, first: 1 });
    DateUtils.convertToIntString = jest
      .fn()
      .mockReturnValue("1 janv. 2022, 12:00:00");

    const $t = (param) => param;
    const $route = { params: { itemState: "", fullPath: "fullPath" } };
    const $router = {
      push: jest.fn(),
    };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const organizations = [{ name: "College" }, { name: "Lycée" }];

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
          $router,
        },
        provide: {
          organizations() {
            return organizations;
          },
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(wrapper.vm.items.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(1);
        wrapper.vm.itemDetail(item1);
        expect($router.push).toHaveBeenCalledTimes(1);
        expect($router.push).toHaveBeenCalledWith({
          name: "ContentDetail",
          params: { id: item1.id },
        });
      });
      expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1);
      expect(ItemService.query).toHaveBeenCalledTimes(1);
      expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1);
      done();
    });
  });

  it("test 4 Owned - update", (done) => {
    const item1 = {
      type: "ATTACHMENT",
      id: 39,
      createdBy: { displayName: "Administrator" },
      createdDate: "2022-01-28T16:04:50Z",
      lastModifiedBy: { displayName: "Administrator" },
      lastModifiedDate: "2022-01-28T16:04:50Z",
      title: "PREMIER TEST VIABLE",
      enclosure: null,
      endDate: "2022-01-29",
      startDate: "2022-01-28",
      validatedBy: { displayName: "Administrator" },
      validatedDate: "2022-01-29",
      status: "DRAFT",
      summary: "DESCRIPTION DU PREMIER TEST VIABLE",
      rssAllowed: false,
      highlight: false,
      organization: { name: "Collège De L'Iroise" },
      redactor: { displayName: "Administrator" },
    };
    ItemService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => "link",
        },
      })
    );
    ClassificationService.highlighted = jest.fn().mockReturnValue(
      Promise.resolve({
        data: { name: "À la une" },
      })
    );
    EnumDatasService.getItemStatusList = jest.fn().mockReturnValue([
      { id: 0, name: "PENDING", label: "enum.itemStatus.pending.title" },
      { id: 4, name: "DRAFT", label: "enum.itemStatus.draft.title" },
    ]);
    ParseLinkUtils.parse = jest.fn().mockReturnValue({ last: 1, first: 1 });
    DateUtils.convertToIntString = jest
      .fn()
      .mockReturnValue("1 janv. 2022, 12:00:00");

    const $t = (param) => param;
    const $route = { params: { itemState: "", fullPath: "fullPath" } };
    const $router = {
      push: jest.fn(),
    };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const organizations = [{ name: "College" }, { name: "Lycée" }];

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
          $router,
        },
        provide: {
          organizations() {
            return organizations;
          },
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(wrapper.vm.items.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(1);
        wrapper.vm.update(item1.id);
        expect($router.push).toHaveBeenCalledTimes(1);
        expect($router.push).toHaveBeenCalledWith({
          name: "PublishPublisher",
          params: { id: item1.id },
        });
      });
      expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1);
      expect(ItemService.query).toHaveBeenCalledTimes(1);
      expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1);
      done();
    });
  });

  it("test 5 Owned - deleteItem", (done) => {
    const item1 = {
      type: "ATTACHMENT",
      id: 39,
      createdBy: { displayName: "Administrator" },
      createdDate: "2022-01-28T16:04:50Z",
      lastModifiedBy: { displayName: "Administrator" },
      lastModifiedDate: "2022-01-28T16:04:50Z",
      title: "PREMIER TEST VIABLE",
      enclosure: null,
      endDate: "2022-01-29",
      startDate: "2022-01-28",
      validatedBy: { displayName: "Administrator" },
      validatedDate: "2022-01-29",
      status: "DRAFT",
      summary: "DESCRIPTION DU PREMIER TEST VIABLE",
      rssAllowed: false,
      highlight: false,
      organization: { name: "Collège De L'Iroise" },
      redactor: { displayName: "Administrator" },
    };
    ItemService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [item1]),
        headers: {
          get: () => "link",
        },
      })
    );
    ClassificationService.highlighted = jest.fn().mockReturnValue(
      Promise.resolve({
        data: { name: "À la une" },
      })
    );
    EnumDatasService.getItemStatusList = jest.fn().mockReturnValue([
      { id: 0, name: "PENDING", label: "enum.itemStatus.pending.title" },
      { id: 4, name: "DRAFT", label: "enum.itemStatus.draft.title" },
    ]);
    ParseLinkUtils.parse = jest.fn().mockReturnValue({ last: 1, first: 1 });
    DateUtils.convertToIntString = jest
      .fn()
      .mockReturnValue("1 janv. 2022, 12:00:00");

    ItemService.get = jest
      .fn()
      .mockReturnValue(Promise.resolve({ data: item1 }));
    ContentService.delete = jest.fn().mockReturnValue(Promise.resolve({}));
    const $t = (param) => param;
    const $route = { params: { itemState: "", fullPath: "fullPath" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const organizations = [{ name: "College" }, { name: "Lycée" }];

    const wrapper = shallowMount(Owned, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        provide: {
          organizations() {
            return organizations;
          },
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(wrapper.vm.items.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(1);
        wrapper.vm.deleteItem(item1.id);
        expect(ItemService.get).toHaveBeenCalledTimes(1);
        wrapper.vm.confirmDelete(item1.id);
        expect(ContentService.delete).toHaveBeenCalledTimes(1);
      });
      expect(EnumDatasService.getItemStatusList).toHaveBeenCalledTimes(1);
      expect(ItemService.query).toHaveBeenCalledTimes(1);
      expect(ClassificationService.highlighted).toHaveBeenCalledTimes(1);
      done();
    });
  });
});
