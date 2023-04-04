import { shallowMount } from "@vue/test-utils";
import RichText from "@/components/richtext/RichText";
// import ClassicEditor from "@ckeditor/ckeditor5-editor-classic/src/classiceditor";
// import MediaEmbed from "@ckeditor/ckeditor5-media-embed/src/mediaembed";
// import { getData as getViewData } from "@ckeditor/ckeditor5-engine/src/dev-utils/view";

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

  /*   it("test 2 RichText - mediaEmbed", async () => {
    const element = document.createElement("div");
    document.body.appendChild(element);

    const videoUrl = "https://test.fr/POD/video/foo/";

    const editor = await ClassicEditor.create(element, {
      plugins: [MediaEmbed],
      mediaEmbed: {
        extraProviders: [
          {
            name: "POD",
            url: /^(?:(?:https?:)?\/\/)?(.*\.fr\/POD\/video)\/(.*)\/(\?is_iframe=true)?$/,
            html: (match) =>
              `<div><iframe src="https://${match[1]}/${match[2]}/?is_iframe=true" width="640" height="360" style="padding: 0; margin: 0; border:0" allowfullscreen ></iframe></div>`,
          },
        ],
      },
    });

    editor.execute("mediaEmbed", videoUrl);

    expect(getViewData(editor.editing.view)).toEqual([
      `<figure class="media"><div data-oembed-url="${videoUrl}"><div><iframe src="${videoUrl}?is_iframe=true" style="padding: 0; margin: 0; border:0" allowfullscreen="" width="640" height="360"></iframe></div></div></figure>`,
    ]);

    await editor.destroy();
  }); */
});
