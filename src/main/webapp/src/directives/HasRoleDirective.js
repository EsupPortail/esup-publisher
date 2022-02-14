import Principal from '@/services/auth/PrincipalService'

// Directive en charge de déterminer si l'utilisateur connecté
// a le role requis pour accéder aux éléments sur l'interface
const HasRoleDirective = {
  hasRole (el, role) {
    role = role.replace(/\s+/g, '')
    var isInRole = Principal.isInRole(role)
    if (!isInRole) {
      el.classList.add('d-none')
    } else {
      el.classList.remove('d-none')
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
