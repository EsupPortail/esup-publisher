import { shallowMount } from '@vue/test-utils'
import Login from '@/views/account/login/Login.vue'
import AuthenticationService from '@/services/auth/AuthenticationService.js'

// Tests unitaires sur la page de Login
describe('Login.vue tests', () => {
  it('test 1 Login - Modal ouverte', done => {
    const loginModalOpenedMock = true
    AuthenticationService.logout = jest.fn().mockReturnValue(Promise.resolve({}))
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

    wrapper.vm.$nextTick(() => {
      expect($store.getters.getLoginModalOpened).toBe(true)
      expect(wrapper.find('#login-modal').exists()).toBe(true)
      expect(AuthenticationService.logout).toHaveBeenCalledTimes(1)
      done()
    })
  })

  it('test 2 Login - Modal fermée', () => {
    const loginModalOpenedMock = false
    AuthenticationService.logout = jest.fn().mockReturnValue(Promise.resolve({}))
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
    expect(AuthenticationService.logout).toHaveBeenCalledTimes(0)
  })
})
