import ConfImageSizeService from './ConfImageSizeService'
import ConfFileSizeService from './ConfFileSizeService'

class ConfigurationService {
  confImageSize
  confFileSize

  init () {
    return Promise.all([ConfImageSizeService.query(), ConfFileSizeService.query()]).then(results => {
      if (results && results.length === 2) {
        this.confImageSize = results[0].data.value
        this.confFileSize = results[1].data.value
      }
    }).catch(error => {
      console.error(error)
    })
  }

  getConfUploadImageSize () {
    if (!this.confImageSize) { this.init() }
    return this.confImageSize
  }

  getConfUploadFileSize () {
    if (!this.confFileSize) { this.init() }
    return this.confFileSize
  }
}

export default new ConfigurationService()
