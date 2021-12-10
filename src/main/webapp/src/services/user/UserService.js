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

  canEditCtx () {
    return FetchWrapper.getJson('api/users/perm/edit')
  }

  canDeleteCtx () {
    return FetchWrapper.getJson('api/users/perm/delete')
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

  canHighlight () {
    return FetchWrapper.getJson('api/users/perm/highlight')
  }

  search (data) {
    return FetchWrapper.postJson('api/users/search', data)
  }
}

export default new UserService()
