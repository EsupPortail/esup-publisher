
import OrganizationService from '@/services/entities/organization/OrganizationService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('OrganizationService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 OrganizationService - get', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve([]))

    OrganizationService.get().then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/organizations')
      expect(value).toStrictEqual([])
      done()
    })
  })

  it('test 2 OrganizationService - getById', (done) => {
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    OrganizationService.getById(id).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/organizations/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 3 OrganizationService - update', (done) => {
    FetchWrapper.putJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const data = {
      id: 1
    }
    OrganizationService.update(data).then(value => {
      expect(FetchWrapper.putJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.putJson).toHaveBeenCalledWith('api/organizations', data)
      expect(value).toStrictEqual({})
      done()
    })
  })

  it('test 4 OrganizationService - delete', (done) => {
    FetchWrapper.deleteJson = jest.fn().mockReturnValue(Promise.resolve({}))

    const id = 1
    OrganizationService.delete(id).then(value => {
      expect(FetchWrapper.deleteJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.deleteJson).toHaveBeenCalledWith('api/organizations/' + id)
      expect(value).toStrictEqual({})
      done()
    })
  })
})
