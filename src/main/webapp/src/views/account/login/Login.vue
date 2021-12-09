<template>
    <div id="login-modal" v-if="toggleModal" >
      <LoginModal :showModal="toggleModal" @relog="relog" ref="modalRef"></LoginModal>
    </div>
    <div v-else class="row mx-0">
      <div class="col-lg-4 offset-lg-4">
        <h1>{{ $t("login.title") }}</h1>
        <div class="alert alert-danger">
          {{ $t("login.messages.error.401") }}
        </div>
        <div class="alert alert-danger" v-if="authenticationError">
          {{ $t("login.messages.error.authentication") }}
        </div>
        <form class="form" role="form">
          <button id="login-button" type="button" class="btn btn-primary" @click="login()">
            {{ $t("login.form.button") }}
          </button>
        </form>
      </div>
    </div>
</template>

<script>
import AuthenticationService from '../../../services/auth/AuthenticationService'
import PrincipalService from '../../../services/auth/PrincipalService'
import LoginModal from './LoginModal.vue'

// Objet en charge de la redirection vers le serveur CAS
const relogState = {}

export default {
  name: 'Login',
  components: {
    LoginModal
  },
  data () {
    return {
      // Booléen indiquant si une erreur est arrivée lors de l'authentification
      authenticationError: false
    }
  },
  computed: {
    // Variable en charge de l'ouverture de la modal LoginModal
    toggleModal () {
      return this.$store.getters.getLoginModalOpened
    }
  },
  methods: {
    // Méthode en charge du processus de connexion
    // Une fois connecté, l'utilisateur est redirigé
    login () {
      AuthenticationService.login()
        .then(() => {
          this.authenticationError = true
          if (!this.$store.getters.getReturnRoute) {
            this.$router.push({ name: 'Home' })
          } else {
            this.$router.push({
              name: this.$store.getters.getReturnRoute.name,
              params: this.$store.getters.getReturnRoute.params
            })
          }
        })
        .catch(() => {
          this.authenticationError = true
          this.relog()
        })
    },

    // Méthode effectuant une redirection sur le serveur CAS,
    // un listener est mis en place afin de détecter la réponse
    // du serveur CAS
    relog (closeLoginModal = true) {
      this.windowOpenCleanup(relogState, closeLoginModal)
      relogState.listener = this.onmessage
      window.addEventListener('message', this.onmessage)

      relogState.window = window.open(process.env.VUE_APP_CAS_LOGIN_URL)
    },

    // Méthode de nettoyage de la page de login
    windowOpenCleanup (state, closeLoginModal) {
      try {
        if (state.listener) {
          window.removeEventListener('message', state.listener)
        }
        if (state.window) {
          state.window.close()
        }
        if (closeLoginModal && this.$store.getters.getLoginModalOpened) {
          this.$store.commit('setLoginModalOpened', false)
        }
      } catch (e) {
        console.error(e)
      }
    },

    // Méthode utilisé lors de la réception de la réponse
    // du serveur CAS puis redirige l'utilisateur
    onmessage (e) {
      if (typeof e.data !== 'string') {
        return
      }
      const m = e.data.match(/^loggedUser=(.*)$/)
      if (!m) {
        return
      }

      this.windowOpenCleanup(relogState, true)
      PrincipalService.identify(true).then()
      if (!this.$store.getters.getReturnRoute) {
        this.$router.push({ name: 'Home' })
      } else {
        this.$router.push({
          name: this.$store.getters.getReturnRoute.name,
          params: this.$store.getters.getReturnRoute.params
        })
      }
    }
  }
}
</script>
