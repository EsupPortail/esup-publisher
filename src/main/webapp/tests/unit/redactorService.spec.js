
import RedactorService from '@/services/entities/redactor/RedactorService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('RedactorService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 RedactorService - query', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    RedactorService.query().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/redactors')
      expect(value).toStrictEqual([])
      done()
    })
  })

  it('test 2 RedactorService - get', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    RedactorService.get(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/redactors/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 3 RedactorService - update', (done) => {
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const data = {
      id: 1
    }
    RedactorService.update(data).then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/redactors', data)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 4 RedactorService - delete', (done) => {
    FetchWrapper.deleteJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    RedactorService.delete(id).then(value => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith('api/redactors/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })
})
