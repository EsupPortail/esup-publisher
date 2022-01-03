import UserService from '@/services/user/UserService.js'
import FetchWrapper from '@/services/util/FetchWrapper.js'

jest.mock('@/services/util/FetchWrapper.js')

// Tests unitaires sur le service UserService
describe('UserService.js tests', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('test 1 UserService - details function OK', (done) => {
    const response = {
      msg: '200 OK'
    }

    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.details('login').then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/extended/login')
      expect(result.msg).toStrictEqual('200 OK')
      done()
    })
  })

  it('test 2 UserService - details function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.details('login').catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/extended/login')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 3 UserService - attributes function OK', (done) => {
    const response = {
      data: ['mail', 'ENTPersonProfils', 'ESCOUAI']
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.attributes().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/attributes')
      expect(result.data.length).toStrictEqual(3)
      done()
    })
  })

  it('test 4 UserService - attributes function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.attributes().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/attributes')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 5 UserService - funtionalAttributes function OK', (done) => {
    const response = {
      data: ['ESCOSIRENCourant', 'uid', 'ENTPersonJointure']
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.funtionalAttributes().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/fnattributes')
      expect(result.data.length).toStrictEqual(3)
      done()
    })
  })

  it('test 6 UserService - funtionalAttributes function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.funtionalAttributes().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/fnattributes')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })
  it('test 7 UserService - canCreateInCtx function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canCreateInCtx().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/createin')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 8 UserService - canCreateInCtx function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canCreateInCtx().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/createin')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 9 UserService - canEditCtx function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canEditCtx().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/edit')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 10 UserService - canEditCtx function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canEditCtx().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/edit')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 11 UserService - canDeleteCtx function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canDeleteCtx().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/delete')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 12 UserService - canDeleteCtx function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canDeleteCtx().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/delete')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 13 UserService - canEditCtxPerms function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canEditCtxPerms().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editPerms')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 14 UserService - canEditCtxPerms function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canEditCtxPerms().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editPerms')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 15 UserService - canEditCtxTargets function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canEditCtxTargets().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editTargets')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 16 UserService - canEditCtxTargets function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canEditCtxTargets().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/editTargets')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 17 UserService - canModerateAnyThing function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canModerateAnyThing().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/moderate')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 18 UserService - canModerateAnyThing function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canModerateAnyThing().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/moderate')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 19 UserService - canHighlight function OK', (done) => {
    const response = {
      data: true
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.canHighlight().then(result => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/highlight')
      expect(result.data).toStrictEqual(true)
      done()
    })
  })

  it('test 20 UserService - canHighlight function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }
    FetchWrapper.getJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.canHighlight().catch(error => {
      expect(FetchWrapper.getJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.getJson).toHaveBeenCalledWith('api/users/perm/highlight')
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })

  it('test 21 UserService - search function OK', (done) => {
    const response = {
      status: '200 OK',
      data: 'Resultat de la recherche'
    }
    const sendingData = {
      criteria: {
        color: 'white'
      }
    }
    FetchWrapper.postJson = jest.fn().mockReturnValue(Promise.resolve(response))

    UserService.search(sendingData).then(result => {
      expect(FetchWrapper.postJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.postJson).toHaveBeenCalledWith('api/users/search', sendingData)
      expect(result.status).toStrictEqual('200 OK')
      done()
    })
  })

  it('test 22 UserService - search function KO', (done) => {
    const response = {
      msg: '401 Unauthorized'
    }

    const sendingData = {
      criteria: {
        color: 'white'
      }
    }
    FetchWrapper.postJson = jest.fn().mockReturnValue(Promise.reject(response))

    UserService.search(sendingData).catch(error => {
      expect(FetchWrapper.postJson).toHaveBeenCalledTimes(1)
      expect(FetchWrapper.postJson).toHaveBeenCalledWith('api/users/search', sendingData)
      expect(error.msg).toStrictEqual('401 Unauthorized')
      done()
    })
  })
})
