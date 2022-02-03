
import GroupService from '@/services/entities/group/GroupService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('GroupService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 GroupService - query without params', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    GroupService.query().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 2 GroupService - query with params', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    GroupService.query({ key: 'value' }).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups?key=value')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 3 GroupService - details', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const id = 1
    GroupService.details(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups/extended/' + id)
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 4 GroupService - attribute', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    GroupService.attributes().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups/attributes')
      expect(value).toStrictEqual(response)
      done()
    })
  })

  it('test 5 GroupService - userMembers', (done) => {
    const response = {
      data: {},
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    GroupService.userMembers().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/groups/usermembers')
      expect(value).toStrictEqual(response)
      done()
    })
  })
})
