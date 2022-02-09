<template>
  <transition :enter-active-class="transition.enter" :leave-active-class="transition.leave">
    <div ref="root" role="alert" aria-live="polite" aria-atomic="true"
      v-show="isActive" class="custom-toast-item"
      :class="[`custom-toast-item-${type}`, `custom-toast-item-${position}`]"
      @mouseover="toggleTimer(true)" @mouseleave="toggleTimer(false)" @click="whenClicked">
      <div class="custom-toast-icon d-block ms-3">
        <i v-if="type === 'info' || type === 'default'" class="fas fa-info-circle fa-2x"></i>
        <i v-else-if="type === 'warning'" class="fas fa-exclamation-triangle fa-2x"></i>
        <i v-else-if="type === 'error'" class="fas fa-times-circle fa-2x"></i>
        <i v-else-if="type === 'success'" class="fas fa-check-circle fa-2x"></i>
      </div>
      <p class="custom-toast-text py-4 px-3" v-html="message"></p>
    </div>
  </transition>
</template>
<script>
import { defineComponent, render } from 'vue'
import eventBus from './Event.js'
import Positions from './Positions.js'
import Timer from './Timer.js'

export default defineComponent({
  name: 'Toaster',
  props: {
    // Message affiché dans le toaster
    message: {
      type: String,
      required: true
    },
    // Type de toaster
    type: {
      type: String,
      default: 'success'
    },
    // Position du toaster
    position: {
      type: String,
      default: Positions.TOP,
      validator (value) {
        return Object.values(Positions).includes(value)
      }
    },
    // Durée d'affichage du toaster
    duration: {
      type: Number,
      default: 5000
    },
    // Suppression du toaster quand on clique dessus
    dismissible: {
      type: Boolean,
      default: true
    },
    // Méthode appelée à la suppression du toaster
    onDismiss: {
      type: Function,
      default: () => {
        // Rien à faire par défaut
      }
    },
    // Méthode appelée au click sur le toaster
    onClick: {
      type: Function,
      default: () => {
        // Rien à faire par défaut
      }
    },
    // Indique si le toaster doit être affiché que lorsque tous les autres ont été supprimés
    queue: Boolean,
    // Indique si le timer doit être mis en pause quand la souris est sur le toaster
    pauseOnHover: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      isActive: false,
      parentTop: null,
      parentBottom: null,
      isHovered: false
    }
  },
  computed: {
    correctParent () {
      switch (this.position) {
        case Positions.TOP:
        case Positions.TOP_RIGHT:
        case Positions.TOP_LEFT:
          return this.parentTop
        case Positions.BOTTOM:
        case Positions.BOTTOM_RIGHT:
        case Positions.BOTTOM_LEFT:
          return this.parentBottom
        default:
          return this.parentTop
      }
    },
    transition () {
      switch (this.position) {
        case Positions.BOTTOM:
        case Positions.BOTTOM_RIGHT:
        case Positions.BOTTOM_LEFT:
          return {
            enter: 'custom-toast-fade-in-up',
            leave: 'custom-toast-fade-out'
          }
        case Positions.TOP:
        case Positions.TOP_RIGHT:
        case Positions.TOP_LEFT:
        default:
          return {
            enter: 'custom-toast-fade-in-down',
            leave: 'custom-toast-fade-out'
          }
      }
    }
  },
  methods: {
    removeElement (el) {
      if (typeof el.remove !== 'undefined') {
        el.remove()
      } else {
        if (el.parentNode) {
          el.parentNode.removeChild(el)
        }
      }
    },
    setupContainer () {
      this.parentTop = document.querySelector('.custom-toast-container.custom-toast-top')
      this.parentBottom = document.querySelector('.custom-toast-container.custom-toast-bottom')
      if (this.parentTop && this.parentBottom) return
      if (!this.parentTop) {
        this.parentTop = document.createElement('div')
        this.parentTop.className = 'custom-toast-container custom-toast-top'
      }
      if (!this.parentBottom) {
        this.parentBottom = document.createElement('div')
        this.parentBottom.className = 'custom-toast-container custom-toast-bottom'
      }
      const container = document.body
      container.appendChild(this.parentTop)
      container.appendChild(this.parentBottom)
    },
    shouldQueue () {
      if (!this.queue) return false
      return (
        this.parentTop.childElementCount > 0 ||
        this.parentBottom.childElementCount > 0
      )
    },
    dismiss () {
      if (this.timer) this.timer.stop()
      clearTimeout(this.queueTimer)
      this.isActive = false
      setTimeout(() => {
        this.onDismiss.apply(null, arguments)
        const wrapper = this.$refs.root
        render(null, wrapper)
        this.removeElement(wrapper)
      }, 150)
    },
    showNotice () {
      if (this.shouldQueue()) {
        this.queueTimer = setTimeout(this.showNotice, 250)
        return
      }
      const wrapper = this.$refs.root.parentElement
      this.correctParent.insertAdjacentElement('afterbegin', this.$refs.root)
      this.removeElement(wrapper)
      this.isActive = true
      if (this.duration) {
        this.timer = new Timer(this.dismiss, this.duration)
      }
    },
    whenClicked () {
      if (!this.dismissible) return
      this.onClick.apply(null, arguments)
      this.dismiss()
    },
    toggleTimer (newVal) {
      if (!this.pauseOnHover || !this.timer) return
      newVal ? this.timer.pause() : this.timer.resume()
    }
  },
  beforeMount () {
    this.setupContainer()
  },
  mounted () {
    this.showNotice()
    eventBus.$on('toast-clear', this.dismiss)
  },
  beforeUnmount () {
    eventBus.$off('toast-clear', this.dismiss)
  }
})
</script>

<style lang="scss">
@keyframes fadeOut {
    from {
        opacity: 1
    }

    to {
        opacity: 0
    }
}

.custom-toast-fade-out {
    animation-name: fadeOut
}

@keyframes fadeInDown {
    from {
        opacity: 0;
        transform: translate3d(0, -100%, 0)
    }

    to {
        opacity: 1;
        transform: none
    }
}

.custom-toast-fade-in-down {
    animation-name: fadeInDown
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translate3d(0, 100%, 0)
    }

    to {
        opacity: 1;
        transform: none
    }
}

.custom-toast-fade-in-up {
    animation-name: fadeInUp
}

.fade-enter-active,.fade-leave-active {
    transition: opacity 150ms ease-out
}

.fade-enter,.fade-leave-to {
    opacity: 0
}

.custom-toast-container {
    position: fixed;
    display: flex;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 1em;
    overflow: hidden;
    z-index: 1056;
    pointer-events: none
}

.custom-toast-item {
    display: inline-flex;
    align-items: center;
    animation-duration: 150ms;
    margin: .5em 0;
    -webkit-box-shadow: 0 0 12px #999999;
    box-shadow: 0 0 12px #999999;
    border-radius: .25em;
    pointer-events: auto;
    opacity: 1;
    color: #fff;
    min-height: 4em;
    cursor: pointer;
}

.custom-toast-item-success {
    background-color: #47d78a
}

.custom-toast-item-info {
    background-color: #1c85d5
}

.custom-toast-item-warning {
    background-color: #febc22
}

.custom-toast-item-error {
    background-color: #f7471c
}

.custom-toast-item-default {
    background-color: #343a40
}

.custom-toast-item.custom-toast-item-top,.custom-toast-item.custom-toast-item-bottom {
    align-self: center
}

.custom-toast-item.custom-toast-item-top-right,.custom-toast-item.custom-toast-item-bottom-right {
    align-self: flex-end
}

.custom-toast-item.custom-toast-item-top-left,.custom-toast-item.custom-toast-item-bottom-left {
    align-self: flex-start
}

.custom-toast-text {
    margin: 0;
    padding: .5em 1em;
    word-break: break-word
}

.custom-toast-icon {
    display: none
}

.custom-toast-container.custom-toast-top {
    flex-direction: column
}

.custom-toast-container.custom-toast-bottom {
    flex-direction: column-reverse
}

.custom-toast-container.custom-toast-custom-parent {
    position: absolute
}

@media screen and (max-width: 768px) {
    .custom-toast-container {
        padding:0;
        position: fixed !important
    }
}
</style>
