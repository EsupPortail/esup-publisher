import UserService from '@/services/user/UserService'

// Directive en charge de déterminer si l'utilisateur a les
// droits d'édition pour un contexte
const CanEditDirective = {
  canEdit (el, context) {
    UserService.canEditCtx(context.keyId, context.keyType).then(response => {
      if (!response.data.value) {
        el.classList.add('d-none')
      } else {
        el.classList.remove('d-none')
      }
    })
  },
  beforeMount (el, { dir, value }) {
    dir.canEdit(el, value)
  }
}

export default CanEditDirective
