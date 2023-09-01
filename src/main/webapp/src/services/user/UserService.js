import FetchWrapper from '../util/FetchWrapper';

class UserService {
  details(login) {
    return FetchWrapper.getJson('api/users/extended/' + login);
  }

  attributes() {
    return FetchWrapper.getJson('api/users/attributes');
  }

  funtionalAttributes() {
    return FetchWrapper.getJson('api/users/fnattributes');
  }

  canCreateInCtx(keyId, keyType) {
    return FetchWrapper.getJson(
      'api/users/perm/createin?' +
        new URLSearchParams({
          keyId: keyId,
          keyType: keyType,
        }),
    );
  }

  canEditCtx(keyId, keyType) {
    return FetchWrapper.getJson(
      'api/users/perm/edit?' +
        new URLSearchParams({
          keyId: keyId,
          keyType: keyType,
        }),
    );
  }

  canDeleteCtx(keyId, keyType) {
    return FetchWrapper.getJson(
      'api/users/perm/delete?' +
        new URLSearchParams({
          keyId: keyId,
          keyType: keyType,
        }),
    );
  }

  canEditCtxPerms(keyId, keyType) {
    return FetchWrapper.getJson(
      'api/users/perm/editPerms?' +
        new URLSearchParams({
          keyId: keyId,
          keyType: keyType,
        }),
    );
  }

  canEditCtxTargets(keyId, keyType) {
    return FetchWrapper.getJson(
      'api/users/perm/editTargets?' +
        new URLSearchParams({
          keyId: keyId,
          keyType: keyType,
        }),
    );
  }

  canModerateAnyThing() {
    return FetchWrapper.getJson('api/users/perm/moderate');
  }

  canHighlight(keyId, keyType) {
    return FetchWrapper.getJson(
      'api/users/perm/highlight?' +
        new URLSearchParams({
          keyId: keyId,
          keyType: keyType,
        }),
    );
  }

  search(data) {
    return FetchWrapper.postJson('api/users/search', data);
  }
}

export default new UserService();
