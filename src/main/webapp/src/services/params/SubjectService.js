import UserService from '@/services/user/UserService'
import GroupService from '@/services/entities/group/GroupService'

class SubjectService {
    userDisplayedAttrs = null
    groupDisplayedAttrs = null
    UserFonctionalAttrs = null

    init () {
      if (!this.userDisplayedAttrs || !this.groupDisplayedAttrs || !this.UserFonctionalAttrs) {
        return Promise.all([UserService.attributes(), GroupService.attributes(), UserService.funtionalAttributes()]).then(results => {
          if (results && results.length === 3) {
            this.userDisplayedAttrs = results[0].data
            this.groupDisplayedAttrs = results[1].data
            this.UserFonctionalAttrs = results[2].data
          }
        }).catch(error => {
          // eslint-disable-next-line
          console.error(error)
        })
      } else {
        return Promise.resolve()
      }
    }

    getUserDisplayedAttrs () {
      if (!this.userDisplayedAttrs) { this.init() }
      return this.userDisplayedAttrs
    }

    getGroupDisplayedAttrs () {
      if (!this.groupDisplayedAttrs) { this.init() }
      return this.groupDisplayedAttrs
    }

    getUserFonctionalAttrs () {
      if (!this.UserFonctionalAttrs) { this.init() }
      return this.UserFonctionalAttrs
    }

    getUserInfos (id) {
      return UserService.details(id)
    }

    getGroupInfos (id) {
      return GroupService.details(id)
    }

    getSubjectInfos (type, id) {
      switch (type) {
        case 'PERSON' : return this.getUserInfos(id)
        case 'GROUP' : return this.getGroupInfos(id)
        default: return Promise.resolve({})
      }
    }
}

export default new SubjectService()
