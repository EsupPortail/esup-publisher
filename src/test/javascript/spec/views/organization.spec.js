import { shallowMount, RouterLinkStub } from "@vue/test-utils";
import Organization from "@/views/entities/organization/Organization.vue";
import OrganizationService from "@/services/entities/organization/OrganizationService";

jest.mock("@/services/entities/enum/EnumDatasService.js");

// Tests unitaires sur la page Owned
describe("Organization.vue tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it("test 1 Organization - Affichage d'un élément dans la liste des organizations", (done) => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };

    OrganizationService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      })
    );

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };

    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          "has-any-role": {},
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(OrganizationService.query).toHaveBeenCalledTimes(1);
      expect(wrapper.vm.organizations.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(1);
      });
      done();
    });
  });

  it("test 2 Organization - Affichage d'éléments dans la liste des organizations", (done) => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };

    const organization2 = {
      id: 2,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Lycée De L'Iroise",
      displayName: "Lycée De L'Iroise",
      description: "Contexte de publication : Lycée De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };
    OrganizationService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1, organization2]),
      })
    );

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          "has-any-role": {},
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(OrganizationService.query).toHaveBeenCalledTimes(1);
      expect(wrapper.vm.organizations.length).toStrictEqual(2);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        expect(
          wrapper.element.querySelectorAll("tbody>tr").length
        ).toStrictEqual(2);
      });
      done();
    });
  });

  it("test 3 Organization - createOrganization", (done) => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };

    OrganizationService.query = jest
      .fn()
      .mockReturnValueOnce(
        Promise.resolve({
          data: [],
        })
      )
      .mockReturnValue(
        Promise.resolve({
          data: Object.assign([], [organization1]),
        })
      );

    OrganizationService.update = jest.fn().mockReturnValue(Promise.resolve({}));
    OrganizationService.delete = jest.fn().mockReturnValue(Promise.resolve({}));

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          "has-any-role": {},
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(OrganizationService.query).toHaveBeenCalledTimes(1);
      expect(wrapper.vm.organizations.length).toStrictEqual(0);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        wrapper.vm.createOrganization();
        expect(OrganizationService.update).toHaveBeenCalledTimes(1);
        wrapper.vm.$nextTick(() => {
          expect(OrganizationService.query).toHaveBeenCalledTimes(2);
          wrapper.vm.$nextTick(() => {
            expect(wrapper.vm.organizations.length).toStrictEqual(1);
          });
        });
      });
      done();
    });
  });

  it("test 4 Organization - organizationDetail", (done) => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };

    OrganizationService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      })
    );

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $router = {
      push: jest.fn(),
    };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const wrapper = shallowMount(Organization, {
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
        directives: {
          "has-any-role": {},
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(OrganizationService.query).toHaveBeenCalledTimes(1);
      expect(wrapper.vm.organizations.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        wrapper.vm.organizationDetail(organization1.id);
        expect($router.push).toHaveBeenCalledTimes(1);
        expect($router.push).toHaveBeenCalledWith({
          name: "AdminEntityOrganizationDetails",
          params: { id: organization1.id },
        });
      });
      done();
    });
  });

  it("test 5 Organization - update", (done) => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };

    OrganizationService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      })
    );

    OrganizationService.get = jest
      .fn()
      .mockReturnValue(Promise.resolve({ data: organization1 }));

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          "has-any-role": {},
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(OrganizationService.query).toHaveBeenCalledTimes(1);
      expect(wrapper.vm.organizations.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        wrapper.vm.update(organization1.id);
        expect(OrganizationService.get).toHaveBeenCalledTimes(1);
      });
      done();
    });
  });

  it("test 5 Organization - deleteOrganization", (done) => {
    const organization1 = {
      id: 1,
      createdBy: {
        login: "admin",
        displayName: "Administrator",
        enabled: true,
        acceptNotifications: false,
        email: "",
        langKey: "fr",
        createdDate: "2020-01-30T15:02:04Z",
        lastModifiedDate: null,
        subject: { keyId: "admin", keyType: "PERSON" },
      },
      createdDate: "2017-03-01T07:00:00Z",
      lastModifiedDate: "2017-03-01T07:00:00Z",
      name: "Collège De L'Iroise",
      displayName: "Collège De L'Iroise",
      description: "Contexte de publication : Collège De L'Iroise",
      displayOrder: 0,
      allowNotifications: false,
      identifiers: ["0291595B", "19291595700018"],
    };

    OrganizationService.query = jest.fn().mockReturnValue(
      Promise.resolve({
        data: Object.assign([], [organization1]),
      })
    );

    OrganizationService.get = jest
      .fn()
      .mockReturnValue(Promise.resolve({ data: organization1 }));
    OrganizationService.delete = jest.fn().mockReturnValue(Promise.resolve({}));

    const $t = (param) => param;
    const $route = { params: { itemState: "" } };
    const $store = {
      getters: {
        getLanguage: "fr",
      },
    };
    const wrapper = shallowMount(Organization, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
        mocks: {
          $t,
          $store,
          $route,
        },
        directives: {
          "has-any-role": {},
        },
      },
    });

    wrapper.vm.$nextTick(() => {
      expect(OrganizationService.query).toHaveBeenCalledTimes(1);
      expect(wrapper.vm.organizations.length).toStrictEqual(1);
      // Attente du rendu html du v-for
      wrapper.vm.$nextTick(() => {
        wrapper.vm.deleteOrganization(organization1.id);
        expect(OrganizationService.get).toHaveBeenCalledTimes(1);
        wrapper.vm.confirmDelete(organization1.id);
        expect(OrganizationService.delete).toHaveBeenCalledTimes(1);
        wrapper.vm.$nextTick(() => {
          expect(wrapper.vm.organizations.length).toStrictEqual(0);
        });
      });
      done();
    });
  });
});
