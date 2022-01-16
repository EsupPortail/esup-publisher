
import FilterService from '@/services/entities/filter/FilterService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('FilterService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 FilterService - query', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    FilterService.query().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/filters')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 2 FilterService - get', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    FilterService.get(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/filters/' + id)
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 3 FilterService - update', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const data = {
      id: 1
    }
    FilterService.update(data).then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/filters', data)
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 4 FilterService - delete', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.deleteJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    FilterService.delete(id).then(value => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith('api/filters/' + id)
      expect(value).toStrictEqual(response)
      done()
    })
  })
})
