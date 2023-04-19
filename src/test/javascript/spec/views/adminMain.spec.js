import { shallowMount } from "@vue/test-utils";
import AdminMain from "@/views/admin/main/AdminMain";

// Tests unitaires sur la page AdminMain
describe("AdminMain.vue tests", () => {
  it("test 1 AdminMain - Affichage du titre", () => {
    const $t = (param) => param;
    const wrapper = shallowMount(AdminMain, {
      global: {
        mocks: {
          $t,
        },
      },
    });
    expect(wrapper.get("h1").text()).toMatch("admin.main.text");
  });
});
