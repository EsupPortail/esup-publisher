<template>
  <ckeditor :editor="editor" v-model="editorData" :config="editorConfig" @ready="onReady"></ckeditor>
</template>
<script>
import CustomUploadAdapter from './CustomUploadAdapter'
import InsertFilePlugin from './InsertFilePlugin'
import store from '@/store/index.js'
import CKEditor from '@ckeditor/ckeditor5-vue'
import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor'
import EssentialsPlugin from '@ckeditor/ckeditor5-essentials/src/essentials'
import BoldPlugin from '@ckeditor/ckeditor5-basic-styles/src/bold'
import ItalicPlugin from '@ckeditor/ckeditor5-basic-styles/src/italic'
import LinkPlugin from '@ckeditor/ckeditor5-link/src/link'
import ParagraphPlugin from '@ckeditor/ckeditor5-paragraph/src/paragraph'
import Heading from '@ckeditor/ckeditor5-heading/src/heading'
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote'
import Strikethrough from '@ckeditor/ckeditor5-basic-styles/src/strikethrough'
import Underline from '@ckeditor/ckeditor5-basic-styles/src/underline'
import ListStyle from '@ckeditor/ckeditor5-list/src/liststyle'
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment'
import Font from '@ckeditor/ckeditor5-font/src/font'
import RemoveFormat from '@ckeditor/ckeditor5-remove-format/src/removeformat'
import SourceEditing from '@ckeditor/ckeditor5-source-editing/src/sourceediting'
import Image from '@ckeditor/ckeditor5-image/src/image'
import ImageToolbar from '@ckeditor/ckeditor5-image/src/imagetoolbar'
import ImageCaption from '@ckeditor/ckeditor5-image/src/imagecaption'
import ImageStyle from '@ckeditor/ckeditor5-image/src/imagestyle'
import ImageUpload from '@ckeditor/ckeditor5-image/src/imageupload'
import ImageInsert from '@ckeditor/ckeditor5-image/src/imageinsert'
import ImageResize from '@ckeditor/ckeditor5-image/src/imageresize'
import LinkImage from '@ckeditor/ckeditor5-link/src/linkimage'
import MediaEmbed from '@ckeditor/ckeditor5-media-embed/src/mediaembed'
import GeneralHtmlSupport from '@ckeditor/ckeditor5-html-support/src/generalhtmlsupport'

export default {
  name: 'RichText',
  components: {
    ckeditor: CKEditor.component
  },
  props: [
    'modelValue', 'entityId', 'imageSizeMax', 'fileSizeMax', 'errorImageSizeMsg', 'errorFileSizeMsg',
    'callBackSuccess', 'callBackError', 'callBackProgress', 'callBackAbord'
  ],
  data () {
    return {
      editorData: this.modelValue || '',
      editor: ClassicEditor,
      editorConfig: {
        plugins: [
          EssentialsPlugin, BoldPlugin, ItalicPlugin, LinkPlugin, ParagraphPlugin,
          Heading, BlockQuote, Strikethrough, Underline, ListStyle, Alignment,
          Font, RemoveFormat, SourceEditing, Image, ImageToolbar, ImageCaption,
          ImageStyle, ImageUpload, ImageInsert, ImageResize, LinkImage, MediaEmbed,
          GeneralHtmlSupport, InsertFilePlugin
        ],
        toolbar: {
          items: [
            'heading', '|',
            'blockQuote', 'bold', 'italic', '|',
            'strikethrough', 'underline', '|',
            'bulletedList', 'numberedList', 'undo', 'redo', '|',
            'alignment:left', 'alignment:center', 'alignment:right', 'alignment:justify', '|',
            'fontBackgroundColor', 'fontColor', 'removeFormat', '|',
            'sourceEditing', 'imageInsert', 'mediaEmbed', 'link'
          ],
          shouldNotGroupWhenFull: true
        },
        heading: {
          options: [
            { model: 'paragraph', title: 'Paragraph' },
            { model: 'heading1', view: 'h1', title: 'Heading 1' },
            { model: 'heading2', view: 'h2', title: 'Heading 2' },
            { model: 'heading3', view: 'h3', title: 'Heading 3' }
          ]
        },
        image: {
          resizeOptions: [
            {
              name: 'resizeImage:original',
              value: null,
              icon: 'original'
            },
            {
              name: 'resizeImage:25',
              value: '25',
              icon: 'small'
            },
            {
              name: 'resizeImage:50',
              value: '50',
              icon: 'medium'
            },
            {
              name: 'resizeImage:100',
              value: '100',
              icon: 'large'
            }
          ],
          toolbar: [
            'imageTextAlternative', 'toggleImageCaption', 'linkImage', '|',
            'resizeImage', '|',
            'imageStyle:alignLeft', 'imageStyle:alignCenter', 'imageStyle:alignRight'
          ]
        },
        link: {
          decorators: {
            openInNewTab: {
              mode: 'manual',
              label: 'Open in a new tab',
              defaultValue: true,
              attributes: {
                target: '_blank',
                rel: 'noopener noreferrer'
              }
            }
          }
        },
        htmlSupport: {
          // Autorisation de toutes les balises HTML
          allow: [
            {
              name: /.*/,
              attributes: true,
              classes: true,
              styles: true
            }
          ],
          // Interdiction des balises scripts et des attributs avec du javascript
          disallow: [
            {
              attributes: [
                { key: /^on(.*)/i, value: true },
                { key: /.*/, value: /(\b)(on\S+)(\s*)=|javascript:|(<\s*)(\/*)script/i },
                { key: /.*/, value: /data:(?!image\/(png|jpeg|gif|webp))/i }
              ]
            },
            { name: 'script' }
          ]
        },
        language: store.getters.getLanguage
      }
    }
  },
  methods: {
    onReady (editor) {
      // Configuration du CustomUploadAdapter pour les images
      editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(loader, this.entityId, this.imageSizeMax,
          this.errorImageSizeMsg, this.callBackSuccess, this.callBackError, this.callBackProgress, this.callBackAbord)
      }
      // Configuration du CustomUploadAdapter pour les autres fichiers
      editor.plugins.get('InsertFilePlugin').createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(loader, this.entityId, this.fileSizeMax,
          this.errorFileSizeMsg, this.callBackSuccess, this.callBackError, this.callBackProgress, this.callBackAbord)
      }
    }
  },
  watch: {
    'editorData' (newVal) {
      this.$emit('update:modelValue', newVal)
    }
  }
}
</script>