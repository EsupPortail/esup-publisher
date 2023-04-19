import { shallowMount } from "@vue/test-utils";
import RichText from "@/components/richtext/RichText";

// Tests unitaires du composant RichText
describe("RichText.vue tests", () => {
  it("test 1 RichText - editorData", (done) => {
    const wrapper = shallowMount(RichText, {
      global: {
        stubs: {
          Ckeditor: { template: '<div class="ckeditor-stub"></div>' },
        },
      },
      props: {
        modelValue: "<p>test</p>",
      },
    });
    expect(wrapper.find(".ckeditor-stub").exists()).toBe(true);
    expect(wrapper.vm.editorData).toStrictEqual("<p>test</p>");

    wrapper.vm.editorData = "<p>new value</p>";
    wrapper.vm.$nextTick(() => {
      const updateEvent = wrapper.emitted("update:modelValue");
      expect(updateEvent).toHaveLength(1);
      expect(updateEvent[0]).toEqual(["<p>new value</p>"]);
      done();
    });
  });
});
