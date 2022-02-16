import { shallowMount } from '@vue/test-utils'
import Login from '@/views/account/login/Login.vue'

// Tests unitaires sur la page de Login
describe('Login.vue tests', () => {
  it('test 1 Login - Modal ouverte', () => {
    const loginModalOpenedMock = true
    const $store = {
      getters: {
        getLoginModalOpened: loginModalOpenedMock
      }
    }
    const wrapper = shallowMount(Login, {
      global: {
        mocks: {
          $store
        }
      }
    })
    expect($store.getters.getLoginModalOpened).toBe(true)
    expect(wrapper.find('#login-modal').exists()).toBe(true)
  })

  it('test 2 Login - Modal fermée', () => {
    const loginModalOpenedMock = false
    const $store = {
      getters: {
        getLoginModalOpened: loginModalOpenedMock
      }
    }
    const $t = (param) => param
    const wrapper = shallowMount(Login, {
      global: {
        mocks: {
          $store,
          $t
        }
      }
    })
    expect($store.getters.getLoginModalOpened).toBe(false)
    expect(wrapper.find('button').exists()).toBe(true)
    expect(wrapper.get('#login-button').text()).toMatch('login.form.button')
  })
})
