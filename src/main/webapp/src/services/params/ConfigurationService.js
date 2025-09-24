import ConfCKEditorService from './ConfCKEditorService.js';
import ConfFileSizeService from './ConfFileSizeService.js';
import ConfImageSizeService from './ConfImageSizeService.js';
import ConfInjectedService from './ConfInjectedService.js';
import ConfMimeTypesService from './ConfMimeTypesService.js';

class ConfigurationService {
  confImageSize;
  confFileSize;
  confMimeTypes;
  confCKEditor;
  confInjected;

  init() {
    return Promise.all([
      ConfImageSizeService.query(),
      ConfFileSizeService.query(),
      ConfMimeTypesService.query(),
      ConfCKEditorService.query(),
      ConfInjectedService.query(),
    ])
      .then((results) => {
        if (results && results.length === 5) {
          this.confImageSize = results[0].data.value;
          this.confFileSize = results[1].data.value;
          this.confMimeTypes = results[2].data.value;
          this.confCKEditor = results[3].data.value;
          this.confInjected = results[4].data;
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

  getConfInjected() {
    if (!this.confInjected) {
      this.init();
    }
    return this.confInjected;
  }
}

export default new ConfigurationService();
