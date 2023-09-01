import FetchWrapper from '../../util/FetchWrapper';

class SubscriberService {
  query() {
    return FetchWrapper.getJson('api/subscribers');
  }

  get(subjectCtx) {
    return FetchWrapper.getJson(
      'api/subscribers/' +
        subjectCtx.subject_id +
        '/' +
        subjectCtx.subject_type +
        '/' +
        subjectCtx.subject_attribute +
        '/' +
        subjectCtx.ctx_id +
        '/' +
        subjectCtx.ctx_type,
    );
  }

  update(subscriber) {
    return FetchWrapper.putJson('api/subscribers', subscriber);
  }

  delete(subjectCtx) {
    return FetchWrapper.deleteJson(
      'api/subscribers/' +
        subjectCtx.subject_id +
        '/' +
        subjectCtx.subject_type +
        '/' +
        subjectCtx.subject_attribute +
        '/' +
        subjectCtx.ctx_id +
        '/' +
        subjectCtx.ctx_type,
    );
  }

  queryForCtx(ctxType, ctxId) {
    return FetchWrapper.getJson('api/subscribers/' + ctxType + '/' + ctxId);
  }

  save(subscriber) {
    return FetchWrapper.postJson('api/subscribers', subscriber);
  }
}

export default new SubscriberService();
