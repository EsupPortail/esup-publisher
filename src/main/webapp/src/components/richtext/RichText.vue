<template>
  <ckeditor
    :editor="editor"
    v-model="editorData"
    :config="editorConfig"
    @ready="onReady"
  ></ckeditor>
</template>
<script>
import CustomUploadAdapter from "./CustomUploadAdapter";
import IconEditingPlugin from "./IconEditingPlugin";
import InsertFilePlugin from "./InsertFilePlugin";
import store from "@/store/index.js";
import CKEditor from "@ckeditor/ckeditor5-vue";
import ClassicEditor from "@ckeditor/ckeditor5-editor-classic/src/classiceditor";
import EssentialsPlugin from "@ckeditor/ckeditor5-essentials/src/essentials";
import BoldPlugin from "@ckeditor/ckeditor5-basic-styles/src/bold";
import ItalicPlugin from "@ckeditor/ckeditor5-basic-styles/src/italic";
import LinkPlugin from "@ckeditor/ckeditor5-link/src/link";
import ParagraphPlugin from "@ckeditor/ckeditor5-paragraph/src/paragraph";
import Heading from "@ckeditor/ckeditor5-heading/src/heading";
import BlockQuote from "@ckeditor/ckeditor5-block-quote/src/blockquote";
import Strikethrough from "@ckeditor/ckeditor5-basic-styles/src/strikethrough";
import Underline from "@ckeditor/ckeditor5-basic-styles/src/underline";
import ListProperties from "@ckeditor/ckeditor5-list/src/listproperties";
import Alignment from "@ckeditor/ckeditor5-alignment/src/alignment";
import Font from "@ckeditor/ckeditor5-font/src/font";
import RemoveFormat from "@ckeditor/ckeditor5-remove-format/src/removeformat";
import SourceEditing from "@ckeditor/ckeditor5-source-editing/src/sourceediting";
import Image from "@ckeditor/ckeditor5-image/src/image";
import ImageToolbar from "@ckeditor/ckeditor5-image/src/imagetoolbar";
import ImageCaption from "@ckeditor/ckeditor5-image/src/imagecaption";
import ImageStyle from "@ckeditor/ckeditor5-image/src/imagestyle";
import ImageUpload from "@ckeditor/ckeditor5-image/src/imageupload";
import ImageInsert from "@ckeditor/ckeditor5-image/src/imageinsert";
import ImageResize from "@ckeditor/ckeditor5-image/src/imageresize";
import LinkImage from "@ckeditor/ckeditor5-link/src/linkimage";
import MediaEmbed from "@ckeditor/ckeditor5-media-embed/src/mediaembed";
import GeneralHtmlSupport from "@ckeditor/ckeditor5-html-support/src/generalhtmlsupport";
import FileManagerService from "@/services/entities/file/FileManagerService";
import Base64Utils from "@/services/util/Base64Utils";

export default {
  name: "RichText",
  components: {
    ckeditor: CKEditor.component,
  },
  props: [
    "modelValue",
    "entityId",
    "imageSizeMax",
    "fileSizeMax",
    "errorImageSizeMsg",
    "errorFileSizeMsg",
    "callBackSuccess",
    "callBackError",
    "callBackProgress",
    "callBackAbord",
  ],
  data() {
    return {
      editorData: this.modelValue || "",
      editor: ClassicEditor,
      editorConfig: {
        plugins: [
          EssentialsPlugin,
          BoldPlugin,
          ItalicPlugin,
          LinkPlugin,
          ParagraphPlugin,
          Heading,
          BlockQuote,
          Strikethrough,
          Underline,
          ListProperties,
          Alignment,
          Font,
          RemoveFormat,
          SourceEditing,
          Image,
          ImageToolbar,
          ImageCaption,
          ImageStyle,
          ImageUpload,
          ImageInsert,
          ImageResize,
          LinkImage,
          MediaEmbed,
          GeneralHtmlSupport,
          IconEditingPlugin,
          InsertFilePlugin,
        ],
        toolbar: {
          // Définition de la barre d'outils de l'éditeur
          items: [
            "heading",
            "|",
            "blockQuote",
            "bold",
            "italic",
            "|",
            "strikethrough",
            "underline",
            "|",
            "bulletedList",
            "numberedList",
            "undo",
            "redo",
            "|",
            "alignment:left",
            "alignment:center",
            "alignment:right",
            "alignment:justify",
            "|",
            "fontBackgroundColor",
            "fontColor",
            "removeFormat",
            "|",
            "sourceEditing",
            "imageInsert",
            "mediaEmbed",
            "link",
          ],
          shouldNotGroupWhenFull: true,
        },
        heading: {
          // Définition de la liste des types de texte
          options: [
            { model: "paragraph", title: "Paragraph" },
            { model: "heading1", view: "h1", title: "Heading 1" },
            { model: "heading2", view: "h2", title: "Heading 2" },
            { model: "heading3", view: "h3", title: "Heading 3" },
          ],
        },
        image: {
          // Définition des options de redimensionnement
          resizeOptions: [
            {
              name: "resizeImage:original",
              value: null,
              icon: "original",
            },
            {
              name: "resizeImage:25",
              value: "25",
              icon: "small",
            },
            {
              name: "resizeImage:50",
              value: "50",
              icon: "medium",
            },
            {
              name: "resizeImage:100",
              value: "100",
              icon: "large",
            },
          ],
          // Définition de la barre d'options des images
          toolbar: [
            "imageTextAlternative",
            "toggleImageCaption",
            "linkImage",
            "|",
            "resizeImage",
            "|",
            "imageStyle:alignLeft",
            "imageStyle:alignCenter",
            "imageStyle:alignRight",
          ],
        },
        link: {
          // Ajout d'une option "Ouvrir dans un nouvel onglet" lors de l'ajout de lien
          decorators: {
            openInNewTab: {
              mode: "manual",
              label: "Open in a new tab",
              defaultValue: true,
              attributes: {
                target: "_blank",
                rel: "noopener noreferrer",
              },
            },
          },
        },
        htmlSupport: {
          allow: [
            // On autorise toutes les balises sauf <i> (gérée par IconEditingPlugin), <script> et <iframe> avec toutes
            // les classes et styles possibles ainsi que les attributs :
            // - "aria-*"" (accessibilité)
            // - "ta-*", "contenteditable", "allowfullscreen", "frameborder" (pour les anciens contenus textAngular)
            {
              name: /^((?!(i|script|iframe)).)*$/,
              classes: true,
              styles: true,
              attributes: [
                {
                  key: /^(((aria|ta)-(.*))|contenteditable|allowfullscreen|frameborder)$/,
                  value: true,
                },
              ],
            },
          ],
        },
        mediaEmbed: {
          // Preview des médias directement dans l'output du ckeditor
          previewsInData: true,
          // Suppression des providers sans preview
          removeProviders: [
            "instagram",
            "twitter",
            "googleMaps",
            "flickr",
            "facebook",
          ],
          // Prise en charge de POD
          extraProviders: [
            {
              name: "POD",
              url: /^(?:(?:https?:)?\/\/)?(pod\.recia\.fr\/video|.*\.fr\/POD\/video)\/(.*)\/(\?is_iframe=true)?$/,
              html: (match) => `<div>
                  <iframe src="https://${match[1]}/${match[2]}/?is_iframe=true" width="640" height="360" style="padding: 0; margin: 0; border:0" allowfullscreen ></iframe>
                </div>`,
            },
          ],
        },
        language: store.getters.getLanguage,
      },
      uploadedFiles: [],
    };
  },
  inject: ["publisher"],
  methods: {
    onReady(editor) {
      // Get already uploaded files
      this.uploadedFiles = this.getUploadedFiles(this.editorData);
      // Configuration du CustomUploadAdapter pour les images
      editor.plugins.get("FileRepository").createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(
          loader,
          this.entityId,
          this.imageSizeMax,
          this.errorImageSizeMsg,
          this.callBackSuccess,
          this.callBackError,
          this.callBackProgress,
          this.callBackAbord
        );
      };
      // Configuration du CustomUploadAdapter pour les autres fichiers
      editor.plugins.get("InsertFilePlugin").createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(
          loader,
          this.entityId,
          this.fileSizeMax,
          this.errorFileSizeMsg,
          this.callBackSuccess,
          this.callBackError,
          this.callBackProgress,
          this.callBackAbord
        );
      };
    },
    checkRemovedFiles(editorData) {
      const newUplodedFiles = this.getUploadedFiles(editorData);
      const diff = this.uploadedFiles.filter(
        (x) => !newUplodedFiles.includes(x)
      );
      this.deleteRemovedFiles(diff);
      this.uploadedFiles = newUplodedFiles;
    },
    async deleteRemovedFiles(files) {
      files.forEach((fileURI) => {
        const isPublic = fileURI.startsWith("files/") ? true : false;
        FileManagerService.delete(
          this.publisher.context.organization.id,
          isPublic,
          Base64Utils.encode(fileURI)
        );
      });
    },
    getUploadedFiles(editorData) {
      const uploadedFiles = editorData.match(
        /files\/(.*?).[a-z]{2,4}|view\/file\/(.*?).[a-z]{2,4}/g
      );

      return uploadedFiles ? uploadedFiles : [];
    },
  },
  watch: {
    editorData(newVal) {
      this.checkRemovedFiles(newVal);
      this.$emit("update:modelValue", newVal);
    },
  },
};
</script>
