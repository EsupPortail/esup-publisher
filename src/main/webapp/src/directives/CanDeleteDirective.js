import UserService from '@/services/user/UserService'

// Directive en charge de déterminer si l'utilisateur a les
// droits de suppression pour un contexte
const CanDeleteDirective = {
  canDelete (el, context) {
    UserService.canDeleteCtx(context.keyId, context.keyType).then(response => {
      if (!response.data.value) {
        el.classList.add('d-none')
      } else {
        el.classList.remove('d-none')
      }
    })
  },
  beforeMount (el, { dir, value }) {
    dir.canDelete(el, value)
  }
}

export default CanDeleteDirective
