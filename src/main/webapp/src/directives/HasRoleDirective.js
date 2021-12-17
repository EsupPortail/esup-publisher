import Principal from '@/services/auth/PrincipalService'

// Directive en charge de déterminer si l'utilisateur connecté
// a le role requis pour accéder aux éléments sur l'interface
const HasRoleDirective = {
  hasRole (el, role) {
    var isInRole = Principal.isInRole(role.toUpperCase())
    if (!isInRole) {
      el.style.display = 'none'
    } else {
      el.style.display = 'inline-block'
    }
  },
  updated (el, { dir, value }) {
    dir.hasRole(el, value)
  },
  beforeMount (el, { dir, value }) {
    dir.hasRole(el, value)
  }
}

export default HasRoleDirective
