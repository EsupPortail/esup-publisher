import FetchWrapper from '../util/FetchWrapper';

class ConfCKEditorService {
  query() {
    return FetchWrapper.getJson('api/conf/ckeditor');
  }
}

export default new ConfCKEditorService();
