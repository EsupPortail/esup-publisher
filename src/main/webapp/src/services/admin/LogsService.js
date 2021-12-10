import FetchWrapper from '../util/FetchWrapper'

class LogsService {
  findAll () {
    return FetchWrapper.getJson('api/logs')
  }

  changeLevel (name, level) {
    return FetchWrapper.putJson('api/logs', { name: name, level: level })
  }
}

export default new LogsService()
