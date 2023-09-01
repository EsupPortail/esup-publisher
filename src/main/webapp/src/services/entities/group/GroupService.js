import FetchWrapper from '../../util/FetchWrapper';

class GroupService {
  query(params) {
    if (params) {
      return FetchWrapper.getJson('api/groups?' + new URLSearchParams(params));
    } else {
      return FetchWrapper.getJson('api/groups');
    }
  }

  details(id) {
    return FetchWrapper.getJson('api/groups/extended/' + id);
  }

  attributes() {
    return FetchWrapper.getJson('api/groups/attributes');
  }

  userMembers(id) {
    return FetchWrapper.getJson('api/groups/usermembers?id=' + id);
  }

  search(data) {
    return FetchWrapper.postJson('api/groups', data);
  }
}

export default new GroupService();
