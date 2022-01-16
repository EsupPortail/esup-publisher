import { h, render } from 'vue'
import Toaster from './Toaster.vue'
import eventBus from './Event.js'

function createComponent (component, props, parentContainer, slots = {}) {
  const vNode = h(component, props, slots)
  const container = document.createElement('div')
  container.classList.add('custom-toast-pending')
  parentContainer.appendChild(container)
  render(vNode, container)

  return vNode.component
}

export const useToast = (globalProps = {}) => {
  return {
    open (options) {
      let message = null
      if (typeof options === 'string') {
        message = options
      }
      const propsData = Object.assign({}, { message }, globalProps, options)
      const instance = createComponent(Toaster, propsData, document.body)
      return {
        dismiss: instance.ctx.dismiss
      }
    },
    clear () {
      eventBus.$emit('toast-clear')
    },
    success (message, options = {}) {
      return this.open(Object.assign({}, {
        message,
        type: 'success'
      }, options))
    },
    error (message, options = {}) {
      return this.open(Object.assign({}, {
        message,
        type: 'error'
      }, options))
    },
    info (message, options = {}) {
      return this.open(Object.assign({}, {
        message,
        type: 'info'
      }, options))
    },
    warning (message, options = {}) {
      return this.open(Object.assign({}, {
        message,
        type: 'warning'
      }, options))
    },
    default (message, options = {}) {
      return this.open(Object.assign({}, {
        message,
        type: 'default'
      }, options))
    }
  }
}
