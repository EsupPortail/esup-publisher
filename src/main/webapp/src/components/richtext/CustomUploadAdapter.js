import UploadUtils from "@/services/util/UploadUtils";
import Compressor from "compressorjs";

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

  async upload() {
    const { entityId, fileSizeMax, isPublic } = this;
    return this.loader.file
      .then(async (file) =>
        file.type.match("image/*") !== null
          ? await this.compressImage(file)
          : file
      )
      .then(
        (file) =>
          new Promise((resolve, reject) => {
            if (!fileSizeMax || file.size <= fileSizeMax) {
              this.xhr = UploadUtils.upload(
                "app/upload/",
                {
                  file: file,
                  isPublic: isPublic(file),
                  entityId: entityId,
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

  isPublic(file) {
    return (
      file.type.match("image/*") !== null ||
      file.type.match("audio/*") !== null ||
      file.type.match("video/*") !== null
    );
  }

  compressImage(file) {
    return new Promise((resolve, reject) => {
      new Compressor(file, {
        quality: 0.8,
        maxWidth: 800,
        maxHeight: 600,
        convertTypes: "image/jpeg",
        async success(blob) {
          resolve(new File([blob], blob.name, { type: blob.type }));
        },
        error(err) {
          reject(err);
        },
      });
    });
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
