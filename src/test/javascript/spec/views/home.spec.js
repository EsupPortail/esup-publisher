import { shallowMount, RouterLinkStub } from '@vue/test-utils'
import Home from '@/views/Home.vue'
import UserService from '@/services/user/UserService'

// Tests unitaires sur la page Home
describe('Home.vue tests', () => {
  it('test 1 Home - Affichage des éléments du menu avec modération', done => {
    UserService.canModerateAnyThing = jest.fn().mockReturnValue(Promise.resolve({
      data: {
        value: true
      }
    }))
    const $t = (param) => param
    const $store = {
      getters: {
        getIdentity: {
          roles: ['ROLE_ADMIN']
        }
      }
    }

    const wrapper = shallowMount(Home, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub
        },
        mocks: {
          $t,
          $store
        },
        directives: {
          'has-role': {}
        }
      }
    })

    wrapper.vm.$nextTick(() => {
      expect(wrapper.find('#publish-publisher-item').exists()).toBe(true)
      expect(wrapper.find('#owned-item').exists()).toBe(true)
      expect(wrapper.find('#pending-item').exists()).toBe(true)
      expect(wrapper.find('#treeview-item').exists()).toBe(true)
      expect(wrapper.find('#administration-item').exists()).toBe(true)
      expect(UserService.canModerateAnyThing).toHaveBeenCalledTimes(1)
      done()
    })
  })

  it('test 2 Home - Affichage des éléments du menu sans modération', done => {
    UserService.canModerateAnyThing = jest.fn().mockReturnValue(Promise.resolve({
      data: {
        value: false
      }
    }))
    const $t = (param) => param
    const $store = {
      getters: {
        getIdentity: {
          roles: ['ROLE_ADMIN']
        }
      }
    }

    const wrapper = shallowMount(Home, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub
        },
        mocks: {
          $t,
          $store
        },
        directives: {
          'has-role': {}
        }
      }
    })

    wrapper.vm.$nextTick(() => {
      expect(wrapper.find('#publish-publisher-item').exists()).toBe(true)
      expect(wrapper.find('#owned-item').exists()).toBe(true)
      expect(wrapper.find('#pending-item').exists()).toBe(false)
      expect(wrapper.find('#treeview-item').exists()).toBe(false)
      expect(wrapper.find('#administration-item').exists()).toBe(true)
      expect(UserService.canModerateAnyThing).toHaveBeenCalledTimes(1)
      done()
    })
  })
})
