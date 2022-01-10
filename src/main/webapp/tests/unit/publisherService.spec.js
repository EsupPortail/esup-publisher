
import PublisherService from '@/services/entities/publisher/PublisherService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'
import DateUtils from '@/services/util/DateUtils'

jest.mock('@/services/util/FetchWrapper.js')

describe('PublisherService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 PublisherService - query', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    PublisherService.query().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers')
      expect(value).toStrictEqual([])
      done()
    })
  })

  it('test 2 PublisherService - get', (done) => {
    const res = {
      id: 1,
      createdDate: '2022-01-20T10:30:44Z',
      lastModifiedDate: '2022-04-12T11:58:02Z'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(res))

    const id = 1
    PublisherService.get(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers/' + id)
      expect(value).toStrictEqual({
        id: res.id,
        createdDate: DateUtils.convertDateTimeFromServer(res.createdDate),
        lastModifiedDate: DateUtils.convertDateTimeFromServer(res.lastModifiedDate)
      })
      done()
    })
  })

  it('test 3 PublisherService - update', (done) => {
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const data = {
      id: 1
    }
    PublisherService.update(data).then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/publishers', data)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 4 PublisherService - delete', (done) => {
    FetchWrapper.deleteJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    PublisherService.delete(id).then(value => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith('api/publishers/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })
})
