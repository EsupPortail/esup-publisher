
import ContextService from '@/services/entities/context/ContextService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

describe('ContextService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 ContextService - query', (done) => {
    const response = {
      data: [],
      headers: []
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    const search = 1
    ContextService.query(search).then(value => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/contexts?search=' + search)
      expect(value).toStrictEqual(response)
      done()
    })
  })
})
