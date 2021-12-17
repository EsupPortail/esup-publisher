import UserService from '@/services/user/UserService'

// Directive en charge de déterminer si l'utilisateur a les
// droits de modération
const CanModerateDirective = {
  canModerate (el) {
    UserService.canModerateAnyThing().then(response => {
      if (!response.value) {
        el.style.display = 'none'
      } else {
        el.style.display = 'inline-block'
      }
    })
  },
  updated (el, { dir }) {
    dir.canModerate(el)
  },
  beforeMount (el, { dir }) {
    dir.canModerate(el)
  }
}

export default CanModerateDirective
