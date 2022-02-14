import UserService from '@/services/user/UserService'

// Directive en charge de déterminer si l'utilisateur a les
// droits de mise à la une pour un contexte
const CanHighlightDirective = {
  canHighlight (el, context) {
    UserService.canHighlight(context.keyId, context.keyType).then(response => {
      if (!response.data.value) {
        el.classList.add('d-none')
      } else {
        el.classList.remove('d-none')
      }
    })
  },
  beforeMount (el, { dir, value }) {
    dir.canHighlight(el, value)
  }
}

export default CanHighlightDirective
