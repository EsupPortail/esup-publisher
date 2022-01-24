
import PublisherService from '@/services/entities/publisher/PublisherService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'
import DateUtils from '@/services/util/DateUtils'

jest.mock('@/services/util/FetchWrapper.js')

describe('PublisherService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 PublisherService - query', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    PublisherService.query().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 2 PublisherService - query with params', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    PublisherService.query({ key1: 'val1', key2: 'val2' }).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers?key1=val1&key2=val2')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 3 PublisherService - get', (done) => {
    const response = {
      data: {
        id: 1,
        createdDate: '2022-01-20T10:30:44Z',
        lastModifiedDate: '2022-04-12T11:58:02Z'
      },
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    PublisherService.get(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/publishers/' + id)
      expect(value).toStrictEqual({
        data: {
          id: response.data.id,
          createdDate: DateUtils.convertDateTimeFromServer(response.data.createdDate),
          lastModifiedDate: DateUtils.convertDateTimeFromServer(response.data.lastModifiedDate)
        },
        headers: []
      })
      done()
    })
  })

  it('test 4 PublisherService - update', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const data = {
      id: 1
    }
    PublisherService.update(data).then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/publishers', data)
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 5 PublisherService - delete', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.deleteJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    PublisherService.delete(id).then(value => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith('api/publishers/' + id)
      expect(value).toStrictEqual(response)
      done()
    })
  })
})
