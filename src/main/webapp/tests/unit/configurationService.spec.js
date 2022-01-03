
import ConfigurationService from '@/services/admin/ConfigurationService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('ConfigurationService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 ConfigurationService - get', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve({
      key1: 'val1',
      key2: 'val2'
    }))

    ConfigurationService.get().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('configprops')
      expect(value).toStrictEqual(['val1', 'val2'])
      done()
    })
  })

  it('test 2 ConfigurationService - getEnv', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve({}))

    ConfigurationService.getEnv().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('env')
      expect(value).toStrictEqual({})
      done()
    })
  })
})
