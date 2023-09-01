import ConfImageSizeService from './ConfImageSizeService';
import ConfFileSizeService from './ConfFileSizeService';
import ConfMimeTypesService from './ConfMimeTypesService';
import ConfCKEditorService from './ConfCKEditorService';

class ConfigurationService {
  confImageSize;
  confFileSize;
  confMimeTypes;
  confCKEditor;

  init() {
    return Promise.all([
      ConfImageSizeService.query(),
      ConfFileSizeService.query(),
      ConfMimeTypesService.query(),
      ConfCKEditorService.query(),
    ])
      .then((results) => {
        if (results && results.length === 4) {
          this.confImageSize = results[0].data.value;
          this.confFileSize = results[1].data.value;
          this.confMimeTypes = results[2].data.value;
          this.confCKEditor = results[3].data.value;
        }
      })
      .catch((error) => {
        // eslint-disable-next-line
        console.error(error);
      });
  }

  getConfUploadImageSize() {
    if (!this.confImageSize) {
      this.init();
    }
    return this.confImageSize;
  }

  getConfUploadFileSize() {
    if (!this.confFileSize) {
      this.init();
    }
    return this.confFileSize;
  }

  getConfAuthorizedMimeTypes() {
    if (!this.confMimeTypes) {
      this.init();
    }
    return this.confMimeTypes;
  }

  getConfCKEditor() {
    if (!this.confCKEditor) {
      this.init();
    }
    return this.confCKEditor;
  }
}

export default new ConfigurationService();
