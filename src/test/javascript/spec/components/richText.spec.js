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

  /* it("test 2 RichText - mediaEmbed", async (done) => {
    const wrapper = shallowMount(RichText);
    const videoUrl = "https://test.fr/POD/video/foo/";

    const { editorState, $nextTick } = wrapper.vm;

    expect(editorState).toBeDefined();
    editorState.execute("mediaEmbed", videoUrl);
    $nextTick(() => {
      const updateEvent = wrapper.emitted("update:modelValue");
      expect(updateEvent).toHaveLength(1);
      expect(updateEvent[0]).toEqual([
        `<figure class="media"><div data-oembed-url="${videoUrl}"><div><iframe src="${videoUrl}?is_iframe=true" style="padding: 0; margin: 0; border:0" allowfullscreen="" width="640" height="360"></iframe></div></div></figure>`,
      ]);
      done();
    });
  }); */
});
