import { shallowMount } from '@vue/test-utils'
import App from '@/App.vue'

// Tests unitaires sur la page App
describe('App.vue tests', () => {
  it('test 1 App footer - Affichage de la version de l\'application', () => {
    process.env = Object.assign(process.env, { NODE_ENV: 'development', BACK_VERSION: '0.5.14' })
    const $t = (param) => param
    const $store = {
      getters: {
        getCssClass: 'site'
      }
    }
    const wrapper = shallowMount(App, {
      global: {
        mocks: {
          $store,
          $t
        }
      }
    })
    expect(wrapper.find('#footer-back-version').exists()).toBe(true)
  })
})
