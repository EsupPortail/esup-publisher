import { useToast } from './useToast.js'

const ToastPlugin = {
  install: (app, options = {}) => {
    const instance = useToast(options)
    app.config.globalProperties.$toast = instance
    app.provide('$toast', instance)
  }
}

export default ToastPlugin
