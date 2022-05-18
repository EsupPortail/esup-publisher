import Plugin from '@ckeditor/ckeditor5-core/src/plugin'
import { toWidget } from '@ckeditor/ckeditor5-widget/src/utils'

// Plugin pour gérer les icônes dans Ckeditor
class IconEditingPlugin extends Plugin {
  init() {
    this._defineSchema()
    this._defineConverters()
  }

  _defineSchema() {
    // Définition de l'objet 'icon-container' pour stocker un icône dans CKEditor.
    // Permet l'enregistrement de :
    //
    // <i class="iconClass">&nbsp;</i>
    //
    // en :
    //
    // <icon-container iconClass="iconClass"></icon-container>
    this.editor.model.schema.register('icon-container', {
      allowWhere: '$text',
      allowAttributesOf: '$text',
      isInline: true,
      isObject: true,
      allowAttributes: ['iconClass']
    })
  }

  _defineConverters() {
    const conversion = this.editor.conversion

    // Conversion HTML vers objet icon-container
    conversion.for('upcast').elementToElement({
      view: {
        name: 'i',
        classes: [
          { key: /^(fa|fas|mdi)$/, value: true }
        ]
      },
      model: (viewElement, { writer: modelWriter }) => {
        // Récupération de l'attribut 'class' de la balise i
        const iconClass = viewElement.getAttribute('class')
        return modelWriter.createElement( 'icon-container', { iconClass })
      }
    })

    // Conversion icon-container vers HTML pour l'édition
    conversion.for('editingDowncast').elementToElement({
      model: 'icon-container',
      view: (modelItem, { writer: viewWriter }) => {
        const widgetElement = viewWriter.createContainerElement('span', {
          class: 'ck-icon-widget'
        })
        const faUIElement = createIconElement(modelItem, viewWriter)
        viewWriter.insert(viewWriter.createPositionAt(widgetElement, 0), faUIElement)
  
        return toWidget(widgetElement, viewWriter)
      }
    })

    // Conversion icon-container vers HTML pour le texte final
    conversion.for('dataDowncast').elementToElement({
      model: 'icon-container',
      view: (modelItem, { writer: viewWriter }) => createIconElement(modelItem, viewWriter)
    })

    // Méthode créant l'élément HTML à partir de l'objet icon-container
    function createIconElement(modelItem, viewWriter) {
      const iconClass = modelItem.getAttribute('iconClass')
      return viewWriter.createUIElement('icon', {}, function(domDocument) {
				const iconDOMElement = domDocument.createElement('i')
        iconDOMElement.setAttribute('class', iconClass)
        iconDOMElement.setAttribute('aria-hidden', true)
        iconDOMElement.innerHTML = '&nbsp;'

				return iconDOMElement
			})
    }
  }
}

export default IconEditingPlugin
