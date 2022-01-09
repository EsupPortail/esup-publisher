
import ReaderService from '@/services/entities/reader/ReaderService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('ReaderService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 ReaderService - query', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    ReaderService.query().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/readers')
      expect(value).toStrictEqual([])
      done()
    })
  })

  it('test 2 ReaderService - get', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    ReaderService.get(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/readers/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 3 ReaderService - update', (done) => {
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const data = {
      id: 1
    }
    ReaderService.update(data).then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/readers', data)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 4 ReaderService - delete', (done) => {
    FetchWrapper.deleteJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    ReaderService.delete(id).then(value => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith('api/readers/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })
})
