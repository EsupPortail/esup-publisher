import FetchWrapper from '../util/FetchWrapper'

class UserService {
  details (login) {
    return FetchWrapper.getJson('api/users/extended/' + login)
  }

  attributes () {
    return FetchWrapper.getJson('api/users/attributes')
  }

  funtionalAttributes () {
    return FetchWrapper.getJson('api/users/fnattributes')
  }

  canCreateInCtx () {
    return FetchWrapper.getJson('api/users/perm/createin')
  }

  canEditCtx (keyId, keyType) {
    return FetchWrapper.getJson('api/users/perm/edit?' + new URLSearchParams({
      keyId: keyId,
      keyType: keyType
    }))
  }

  canDeleteCtx (keyId, keyType) {
    return FetchWrapper.getJson('api/users/perm/delete?' + new URLSearchParams({
      keyId: keyId,
      keyType: keyType
    }))
  }

  canEditCtxPerms () {
    return FetchWrapper.getJson('api/users/perm/editPerms')
  }

  canEditCtxTargets () {
    return FetchWrapper.getJson('api/users/perm/editTargets')
  }

  canModerateAnyThing () {
    return FetchWrapper.getJson('api/users/perm/moderate')
  }

  canHighlight (keyId, keyType) {
    return FetchWrapper.getJson('api/users/perm/highlight?' + new URLSearchParams({
      keyId: keyId,
      keyType: keyType
    }))
  }

  search (data) {
    return FetchWrapper.postJson('api/users/search', data)
  }
}

export default new UserService()
