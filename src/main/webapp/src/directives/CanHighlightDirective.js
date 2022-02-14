import UserService from '@/services/user/UserService'

// Directive en charge de déterminer si l'utilisateur a les
// droits de mise à la une pour un contexte
const CanHighlightDirective = {
  canHighlight (el, context) {
    if (context) {
      // On ne refait la requête que si les paramètres ont changé
      const keyId = context.keyId
      const keyType = context.keyType
      if (el.canHighlightKeyId !== keyId || el.canHighlightKeyType !== keyType) {
        el.canHighlightKeyId = keyId
        el.canHighlightKeyType = keyType
        el.classList.add('d-none')
        UserService.canHighlight(keyId, keyType).then(response => {
          if (!response.data.value) {
            el.classList.add('d-none')
          } else {
            el.classList.remove('d-none')
          }
        })
      }
    } else {
      el.canHighlightKeyId = undefined
      el.canHighlightKeyType = undefined
      el.classList.add('d-none')
    }
  },
  updated (el, { dir, value }) {
    dir.canHighlight(el, value)
  },
  beforeMount (el, { dir, value }) {
    dir.canHighlight(el, value)
  }
}

export default CanHighlightDirective
