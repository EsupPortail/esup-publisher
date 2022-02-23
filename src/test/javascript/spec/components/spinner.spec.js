import { shallowMount } from '@vue/test-utils'
import Spinner from '@/components/spinner/Spinner'
import FetchWrapper from '@/services/util/FetchWrapper.js'

// Tests unitaires du composant Spinner
describe('Spinner.vue tests', () => {
  it('test 1 Spinner - showSpinner', done => {
    FetchWrapper.countPendingRequests = jest.fn().mockReturnValue(0)
    const $t = (param) => param
    const wrapper = shallowMount(Spinner, {
      global: {
        mocks: {
          $t
        }
      }
    })

    expect(wrapper.find('.spinner-border').exists()).toBe(false)
    expect(wrapper.vm.showSpinner).toStrictEqual(false)

    wrapper.vm.countPendingRequests = 2
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find('.spinner-border').exists()).toBe(true)
      expect(wrapper.vm.showSpinner).toStrictEqual(true)
      done()
    })
  })
})
