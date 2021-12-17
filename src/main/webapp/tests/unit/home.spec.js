import { shallowMount } from '@vue/test-utils'
import Home from '@/views/Home.vue'

// Tests unitaires sur la page Home
describe('Home.vue tests', () => {
  it('test 1 Home - Affichage des éléments du menu', () => {
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
        mocks: {
          $t,
          $store
        },
        directives: {
          'can-moderate': {},
          'has-role': {}
        }
      }
    })
    expect(wrapper.find('#publish-publisher-item').exists()).toBe(true)
    expect(wrapper.find('#owned-item').exists()).toBe(true)
    expect(wrapper.find('#pending-item').exists()).toBe(true)
    expect(wrapper.find('#treeview-item').exists()).toBe(true)
    expect(wrapper.find('#administration-item').exists()).toBe(true)
  })
})
