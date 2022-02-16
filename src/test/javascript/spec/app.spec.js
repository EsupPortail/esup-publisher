import { shallowMount } from '@vue/test-utils'
import App from '@/App.vue'
import ConfigurationService from '@/services/params/ConfigurationService.js'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'

jest.mock('@/services/entities/enum/EnumDatasService.js')

// Tests unitaires sur la page App
describe('App.vue tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 App footer - Affichage de la version de l\'application', () => {
    ConfigurationService.init = jest.fn().mockReturnValue(Promise.resolve([]))
    EnumDatasService.init = jest.fn().mockReturnValue(Promise.resolve([]))
    process.env = Object.assign(process.env, { NODE_ENV: 'development', BACK_VERSION: '0.5.14' })
    const $t = (param) => param
    const $router = {
      currentRoute: {
        value: {
          meta: {
            cssClass: 'site'
          }
        }
      }
    }
    const wrapper = shallowMount(App, {
      global: {
        mocks: {
          $router,
          $t
        }
      }
    })
    expect(wrapper.find('#footer-back-version').exists()).toBe(true)
    expect(ConfigurationService.init).toHaveBeenCalledTimes(1)
    expect(EnumDatasService.init).toHaveBeenCalledTimes(1)
  })
})
