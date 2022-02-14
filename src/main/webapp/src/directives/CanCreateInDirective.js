import UserService from '@/services/user/UserService'

// Directive en charge de déterminer si l'utilisateur a les
// droits de création dans un contexte
const CanCreateInDirective = {
  canCreateIn (el, context) {
    if (context) {
      // On ne refait la requête que si les paramètres ont changé
      const keyId = context.keyId
      const keyType = context.keyType
      if (el.canCreateInKeyId !== keyId || el.canCreateInKeyType !== keyType) {
        el.canCreateInKeyId = keyId
        el.canCreateInKeyType = keyType
        el.classList.add('d-none')
        UserService.canCreateInCtx(keyId, keyType).then(response => {
          if (!response.data.value) {
            el.classList.add('d-none')
          } else {
            el.classList.remove('d-none')
          }
        })
      }
    } else {
      el.canCreateInKeyId = undefined
      el.canCreateInKeyType = undefined
      el.classList.add('d-none')
    }
  },
  updated (el, { dir, value }) {
    dir.canCreateIn(el, value)
  },
  beforeMount (el, { dir, value }) {
    dir.canCreateIn(el, value)
  }
}

export default CanCreateInDirective
