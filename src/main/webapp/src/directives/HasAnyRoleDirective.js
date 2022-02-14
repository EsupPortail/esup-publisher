import Principal from '@/services/auth/PrincipalService'

// Directive en charge de déterminer si l'utilisateur connecté
// a un des roles requis pour accéder aux éléments sur l'interface
const HasAnyRoleDirective = {
  hasAnyRole (el, roles) {
    roles = roles.replace(/\s+/g, '').split(',')
    var isInRole = Principal.isInAnyRole(roles)
    if (!isInRole) {
      el.classList.add('d-none')
    } else {
      el.classList.remove('d-none')
    }
  },
  updated (el, { dir, value }) {
    dir.hasAnyRole(el, value)
  },
  beforeMount (el, { dir, value }) {
    dir.hasAnyRole(el, value)
  }
}

export default HasAnyRoleDirective
