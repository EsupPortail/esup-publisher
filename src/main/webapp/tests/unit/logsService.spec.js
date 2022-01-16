
import LogsService from '@/services/admin/LogsService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('LogsService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 LogsService - findAll', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    LogsService.findAll().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/logs')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 2 LogsService - changeLevel', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve(response))

    LogsService.changeLevel('myName', 'myLevel').then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/logs', { name: 'myName', level: 'myLevel' })
      expect(value).toStrictEqual(response)
      done()
    })
  })
})
