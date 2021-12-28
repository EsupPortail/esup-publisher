import FetchWrapper from '../util/FetchWrapper'

class MonitoringService {
  getMetrics () {
    return FetchWrapper.getJson('metrics/metrics')
  }

  checkHealth () {
    return FetchWrapper.getJson('health')
  }

  threadDump () {
    return FetchWrapper.getJson('threaddump')
  }
}

export default new MonitoringService()
