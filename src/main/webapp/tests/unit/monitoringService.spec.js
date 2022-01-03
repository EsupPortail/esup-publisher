
import MonitoringService from '@/services/admin/MonitoringService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('MonitoringService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 MonitoringService - getMetrics', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    MonitoringService.getMetrics().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('metrics/metrics')
      expect(value).toStrictEqual([])
      done()
    })
  })

  it('test 2 MonitoringService - checkHealth', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    MonitoringService.checkHealth().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('health')
      expect(value).toStrictEqual([])
      done()
    })
  })

  it('test 3 MonitoringService - threadDump', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    MonitoringService.threadDump().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('threaddump')
      expect(value).toStrictEqual([])
      done()
    })
  })
})
