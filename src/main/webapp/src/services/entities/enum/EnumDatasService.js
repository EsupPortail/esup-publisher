import AllEnumsService from './AllEnumsService'
import router from '@/router/index.js'

class EnumDatasService {
  AccessTypeList
  ClassificationDecorTypeList
  ContextTypeList
  DisplayOrderTypeList
  FilterTypeList
  ItemStatusList
  ItemTypeList
  OperatorTypeList
  PermissionClassList
  PermissionTypeList
  StringEvaluationModeList
  SubjectTypeList
  SubscribeTypeList
  WritingModeList
  WritingFormatList

  init () {
    return AllEnumsService.query().then(response => {
      if (response.data) {
        this.AccessTypeList = response.data.AccessType
        this.ContextTypeList = response.data.ContextType
        this.DisplayOrderTypeList = response.data.DisplayOrderType
        this.FilterTypeList = response.data.FilterType
        this.ItemStatusList = response.data.ItemStatus
        this.ItemTypeList = response.data.ItemType
        this.OperatorTypeList = response.data.OperatorType
        this.PermissionClassList = response.data.PermissionClass
        this.PermissionTypeList = response.data.PermissionType
        this.StringEvaluationModeList = response.data.StringEvaluationMode
        this.SubjectTypeList = response.data.SubjectType
        this.SubscribeTypeList = response.data.SubscribeType
        this.WritingModeList = response.data.WritingMode
        this.WritingFormatList = response.data.WritingFormat
        this.ClassificationDecorTypeList = response.data.ClassificationDecorType
      }
    }).catch(error => {
      console.error(error)
      router.push({ name: 'Error' })
    })
  }

  getAccessTypeList () {
    if (!this.AccessTypeList) { this.init() }
    return this.AccessTypeList
  }

  getClassificationDecorTypeList () {
    if (!this.ClassificationDecorTypeList) { this.init() }
    return this.ClassificationDecorTypeList
  }

  getContextTypeList () {
    if (!this.ContextTypeList) { this.init() }
    return this.ContextTypeList
  }

  getDisplayOrderTypeList () {
    if (!this.DisplayOrderTypeList) { this.init() }
    return this.DisplayOrderTypeList
  }

  getFilterTypeList () {
    if (!this.FilterTypeList) { this.init() }
    return this.FilterTypeList
  }

  getItemStatusList () {
    if (!this.ItemStatusList) { this.init() }
    return this.ItemStatusList
  }

  getItemTypeList () {
    if (!this.ItemTypeList) { this.init() }
    return this.ItemTypeList
  }

  getOperatorTypeList () {
    if (!this.OperatorTypeList) { this.init() }
    return this.OperatorTypeList
  }

  getPermissionClassList () {
    if (!this.PermissionClassList) { this.init() }
    return this.PermissionClassList
  }

  getPermissionTypeList () {
    if (!this.PermissionTypeList) { this.init() }
    return this.PermissionTypeList
  }

  getStringEvaluationModeList () {
    if (!this.StringEvaluationModeList) { this.init() }
    return this.StringEvaluationModeList
  }

  getSubjectTypeList () {
    if (!this.SubjectTypeList) { this.init() }
    return this.SubjectTypeList
  }

  getSubscribeTypeList () {
    if (!this.SubscribeTypeList) { this.init() }
    return this.SubscribeTypeList
  }

  getWritingModeList () {
    if (!this.WritingModeList) { this.init() }
    return this.WritingModeList
  }

  getWritingFormatList () {
    if (!this.WritingFormatList) { this.init() }
    return this.WritingFormatList
  }
}

export default new EnumDatasService()
