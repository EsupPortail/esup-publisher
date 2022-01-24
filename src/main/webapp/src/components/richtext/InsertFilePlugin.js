import Plugin from '@ckeditor/ckeditor5-core/src/plugin'
import UploadUtils from '@/services/util/UploadUtils'

// Plugin pour l'upload de fichier (qui ne sont pas des images) dans CKEditor
export default class InsertFilePlugin extends Plugin {
  static get pluginName () {
    return 'InsertFilePlugin'
  }

  init () {
    const editor = this.editor
    editor.editing.view.document.on(
      'drop',
      async (event, data) => {
        // Si le fichier à upload est une image, on ne fait rien (géré par le plugin ImageUpload)
        if (data.dataTransfer.files && !data.dataTransfer.files[0].type.match('image/*')) {
          event.stop()
          data.preventDefault()
          this.insert(data.dataTransfer.files[0], editor)
        }
      },
      { priority: 'high' }
    )

    editor.editing.view.document.on(
      'dragover',
      (event, data) => {
        event.stop()
        data.preventDefault()
      },
      { priority: 'high' }
    )
  }

  insert (file, editor) {
    if (this.createUploadAdapter) {
      const uploader = this.createUploadAdapter({
        file: Promise.resolve(file)
      })
      uploader.upload().then(res => {
        const content = `
          <a href="${res.default}" target="_blank" rel="noopener noreferrer"/>
            <i class="${UploadUtils.getCssFileFromType(file.type, file.name)}" aria-hidden="true">&nbsp;</i>
            <span>${file.name}</span>
          </a>
        `
        const viewFragment = editor.data.processor.toView(content)
        const modelFragment = editor.data.toModel(viewFragment)
        editor.model.insertContent(
          modelFragment,
          editor.model.document.selection
        )
      }).catch(() => {
        // On ne fait rien, géré par l'UploadAdapter
      })
    }
  }
}
