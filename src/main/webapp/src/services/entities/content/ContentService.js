import FetchWrapper from "../../util/FetchWrapper";
import DateUtils from "../../util/DateUtils";

class ContentService {
  query() {
    return FetchWrapper.getJson("api/contents");
  }

  get(id) {
    return FetchWrapper.getJson("api/contents/" + id).then((response) => {
      if (response.data) {
        addBaseUrlToBodyLinks(response.data);
        response.data.item.endDate = DateUtils.convertLocalDateFromServer(
          response.data.item.endDate
        );
        response.data.item.startDate = DateUtils.convertLocalDateFromServer(
          response.data.item.startDate
        );
        response.data.item.validatedDate = DateUtils.convertDateTimeFromServer(
          response.data.item.validatedDate
        );
        response.data.item.createdDate = DateUtils.convertDateTimeFromServer(
          response.data.item.createdDate
        );
        response.data.item.lastModifiedDate =
          DateUtils.convertDateTimeFromServer(
            response.data.item.lastModifiedDate
          );
      }
      return response;
    });
  }

  update(content) {
    const copy = Object.assign({}, content);
    removeBaseUrlToBodyLinks(content);
    copy.item.startDate = DateUtils.convertLocalDateToServer(
      copy.item.startDate
    );
    copy.item.endDate = DateUtils.convertLocalDateToServer(copy.item.endDate);
    return FetchWrapper.putJson("api/contents", copy);
  }

  save(content) {
    const copy = Object.assign({}, content);
    removeBaseUrlToBodyLinks(content);
    copy.item.startDate = DateUtils.convertLocalDateToServer(
      copy.item.startDate
    );
    copy.item.endDate = DateUtils.convertLocalDateToServer(copy.item.endDate);
    return FetchWrapper.postJson("api/contents", copy);
  }

  delete(id) {
    return FetchWrapper.deleteJson("api/contents/" + id);
  }
}

/**
 * Méthode permettant de rajouter dans les liens du body d'un contenu la baseUrl.
 *
 * @param {Objecct} content Objet Contenu avec le body à mettre à jour
 */
function addBaseUrlToBodyLinks(content) {
  if (
    content.item &&
    content.item.body &&
    content.linkedFiles &&
    content.linkedFiles.length > 0
  ) {
    content.linkedFiles.forEach((link) => {
      if (content.item.body.includes('"' + link.uri + '"')) {
        content.item.body = content.item.body.replaceAll(
          '"' + link.uri + '"',
          '"' + process.env.VUE_APP_BACK_BASE_URL + link.uri + '"'
        );
      }
    });
  }
}

/**
 * Méthode permettant de supprimer dans les liens du body d'un contenu la baseUrl.
 *
 * @param {Objecct} content Objet Contenu avec le body à mettre à jour
 */
function removeBaseUrlToBodyLinks(content) {
  if (
    content.item &&
    content.item.body &&
    content.linkedFiles &&
    content.linkedFiles.length > 0
  ) {
    content.linkedFiles.forEach((link) => {
      if (
        content.item.body.includes(
          '"' + process.env.VUE_APP_BACK_BASE_URL + link.uri + '"'
        )
      ) {
        content.item.body = content.item.body.replaceAll(
          '"' + process.env.VUE_APP_BACK_BASE_URL + link.uri + '"',
          '"' + link.uri + '"'
        );
      }
    });
  }
}

export default new ContentService();
