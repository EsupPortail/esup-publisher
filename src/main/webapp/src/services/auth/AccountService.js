import FetchWrapper from '../util/FetchWrapper'

class AccountService {
  account () {
    return FetchWrapper.getJson('api/account')
  }
}

export default new AccountService()
