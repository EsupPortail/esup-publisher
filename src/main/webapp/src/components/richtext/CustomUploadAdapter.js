import UploadUtils from "@/services/util/UploadUtils";

// Upload adpter utilisé pour l'upload de fichier
class CustomUploadAdapter {
  constructor(
    loader,
    entityId,
    fileSizeMax,
    errorFileSizeMsg,
    callBackSuccess,
    callBackError,
    callBackProgress,
    callBackAbord
  ) {
    this.loader = loader;
    this.entityId = entityId;
    this.fileSizeMax = fileSizeMax;
    this.errorFileSizeMsg = errorFileSizeMsg;
    this.callBackSuccess = callBackSuccess;
    this.callBackError = callBackError;
    this.callBackProgress = callBackProgress;
    this.callBackAbord = callBackAbord;
  }

  upload() {
    return this.loader.file.then(
      (file) =>
        new Promise((resolve, reject) => {
          const isPublic =
            file.type.match("image/*") !== null ||
            file.type.match("audio/*") !== null ||
            file.type.match("video/*") !== null;
          if (!this.fileSizeMax || file.size <= this.fileSizeMax) {
            this.xhr = UploadUtils.upload(
              "app/upload/",
              {
                file: file,
                isPublic: isPublic,
                entityId: this.entityId,
                name: file.name,
              },
              (response, headers) => {
                const location = decodeURIComponent(headers.location);
                if (this.callBackSuccess) {
                  this.callBackSuccess(file, location);
                }
                resolve({
                  default: process.env.VUE_APP_BACK_BASE_URL + location,
                });
              },
              (response) => {
                if (this.callBackError) {
                  this.callBackError(response);
                }
                /* eslint-disable-next-line prefer-promise-reject-errors */
                reject();
              },
              (evt) => {
                if (this.callBackProgress) {
                  this.callBackProgress(evt);
                }
              }
            );
          } else {
            if (this.callBackError) {
              this.callBackError(this.errorFileSizeMsg);
            }
            /* eslint-disable-next-line prefer-promise-reject-errors */
            reject();
          }
        })
    );
  }

  abort() {
    if (this.callBackAbord) {
      this.callBackAbord();
    }
    if (this.xhr) {
      this.xhr.abort();
    }
  }
}

export default CustomUploadAdapter;
