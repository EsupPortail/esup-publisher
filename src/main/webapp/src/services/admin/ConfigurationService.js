import FetchWrapper from '../util/FetchWrapper'

class ConfigurationService {
  get () {
    return FetchWrapper.getJson('configprops').then(response => {
      var properties = []
      Object.values(response.data).forEach(data => {
        properties.push(data)
      })
      return properties
    })
  }

  getEnv () {
    return FetchWrapper.getJson('env')
  }
}

export default new ConfigurationService()
