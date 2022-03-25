<template>
  <div class="ctxDetails">
    <ul class="nav nav-pills">
      <li class="nav-item">
        <a class="nav-link" :aria-current="activeNav ==='content' || !activeNav" :class="{ active: activeNav ==='content' || !activeNav }" href="" @click.prevent="showNav('content')">
          <span >{{$t('manager.treeview.details.context.properties')}}</span>
        </a>
      </li>
      <li class="nav-item" v-if="hasPermissionManagment && canManage">
        <a class="nav-link" :aria-current="activeNav ==='permissions'" :class="{ active: activeNav ==='permissions'}" href="" @click.prevent="showNav('permissions')">
          <span>{{$t('manager.treeview.details.permissions')}}</span>
        </a>
      </li>
      <li class="nav-item" v-if="hasTargetManagment && canManage">
        <a class="nav-link" :aria-current="activeNav ==='targets'" :class="{ active: activeNav ==='targets'}" href="" @click.prevent="showNav('targets')">
          <span>{{$t('manager.treeview.details.targets')}}</span>
        </a>
      </li>
    </ul>

    <div class="tab-content">
      <div id="context" class="nav-item" :class="{active: activeNav === 'content'}">
        <TabContext v-if="activeNav === 'content'"></TabContext>
      </div>
      <div id="permissions" class="nav-item" :class="{active: activeNav === 'permissions'}">
        <TabPermissions v-if="activeNav === 'permissions'"></TabPermissions>
      </div>
      <div id="targets" class="nav-item" :class="{active: activeNav === 'targets'}">
        <TabTargets ref="tabTargets" v-if="activeNav === 'targets'"></TabTargets>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, readonly } from 'vue'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import OrganizationService from '@/services/entities/organization/OrganizationService'
import PermissionOnContextService from '@/services/entities/permissionOnContext/PermissionOnContextService'
import PermissionOnClassificationWithSubjectListService from '@/services/entities/permissionOnClassificationWithSubjectList/PermissionOnClassificationWithSubjectListService'
import PublisherService from '@/services/entities/publisher/PublisherService'
import ClassificationService from '@/services/entities/classification/ClassificationService'
import ItemService from '@/services/entities/item/ItemService'
import SubscriberService from '@/services/entities/subscriber/SubscriberService'
import ContentService from '@/services/entities/content/ContentService'
import CategoryService from '@/services/entities/category/CategoryService'
import PrincipalService from '@/services/auth/PrincipalService'
import SubjectService from '@/services/params/SubjectService'
import CommonUtils from '@/services/util/CommonUtils'
import UserService from '@/services/user/UserService'
import TabContext from './context/TabContext'
import TabPermissions from './permissions/TabPermissions'
import TabTargets from './targets/TabTargets'

export default {
  name: 'CtxDetails',
  components: {
    TabContext,
    TabPermissions,
    TabTargets
  },
  data () {
    return {
      activeNav: 'content',
      context: {},
      editedContext: {},
      ctxType: null,
      ctxId: null,
      permissions: {},
      targets: {},
      publisher: { context: { organization: {}, redactor: {}, reader: {} }, displayName: null, defaultDisplayOrder: null, permissionType: null, used: false, displayOrder: 0, hasSubPermsManagement: false, doHighlight: false, id: null },
      subscriber: {},
      selectedSubject: {},
      selectedSubjects: [],
      availableRoles: [],
      autorizedDisplayOrderTypeList: [],
      autorizedPermissionClassList: [],
      search: { subject: {}, target: {} },
      edit: { authorizedSubject: {} },
      permissionAdvanced: false,
      canManage: false,
      form: {},
      hasTargetManagment: false,
      hasPermissionManagment: false,
      ctxPermissionType: null,
      subjectInfosConfig: {
        getSubjectInfos: null,
        resolveKey: true,
        userDisplayedAttrs: null
      },
      permissionTemplate: '',
      canCreateCategory: false,
      paletteColorPicker: ['#F44336', '#E91E63', '#9C27B0', '#673AB7', '#3F51B5', '#2196F3',
        '#03A9F4', '#00BCD4', '#009688', '#4CAF50', '#8BC34A', '#CDDC39',
        '#FFEB3B', '#FFC107', '#FF9800', '#FF5722', '#795548', '#9E9E9E',
        '#607D8B', '#000000'
      ],
      langList: [
        { id: 'fr', label: 'category.langList.fr' },
        { id: 'en', label: 'category.langList.en' },
        { id: 'de', label: 'category.langList.de' },
        { id: 'es', label: 'category.langList.es' }
      ]
    }
  },
  inject: ['deleteTreeNode', 'selectTreeNode', 'refreshTreeNode', 'parentNodeId'],
  provide () {
    return {
      context: readonly(computed(() => this.context)),
      editedContext: readonly(computed(() => this.editedContext)),
      setEditedContext: (editedContext) => { this.editedContext = editedContext },
      publisher: readonly(computed(() => this.publisher)),
      autorizedDisplayOrderTypeList: readonly(computed(() => this.autorizedDisplayOrderTypeList)),
      autorizedPermissionClassList: readonly(computed(() => this.autorizedPermissionClassList)),
      updateContext: this.updateContext,
      confirmDeleteContext: this.confirmDeleteContext,
      confirmUpdateContext: this.confirmUpdateContext,
      createContext: this.createContext,
      canManage: readonly(computed(() => this.canManage)),
      canCreateCategory: readonly(computed(() => this.canCreateCategory)),
      paletteColorPicker: readonly(computed(() => this.paletteColorPicker)),
      langList: readonly(computed(() => this.langList)),
      targets: readonly(computed(() => this.targets)),
      setTargets: (targets) => { this.targets = targets },
      ctxType: readonly(computed(() => this.ctxType)),
      getHasTargetManagment: this.getHasTargetManagment,
      getEnumlabel: this.getEnumlabel,
      getSubjectTypeList: this.getSubjectTypeList,
      permissions: readonly(computed(() => this.permissions)),
      setPermissions: (permissions) => { this.permissions = permissions },
      ctxPermissionType: readonly(computed(() => this.ctxPermissionType)),
      setCtxPermissionType: (ctxPermissionType) => { this.ctxPermissionType = ctxPermissionType },
      availableRoles: readonly(computed(() => this.availableRoles)),
      permissionTypeList: readonly(computed(() => this.permissionTypeList)),
      loadAvailableRoles: this.loadAvailableRoles,
      load: this.load,
      subjectInfosConfig: readonly(computed(() => this.subjectInfosConfig)),
      selectPermissionManager: this.selectPermissionManager,
      operatorTypeList: readonly(computed(() => this.operatorTypeList)),
      stringEvaluationModeList: readonly(computed(() => this.stringEvaluationModeList)),
      permissionTemplate: readonly(computed(() => this.permissionTemplate))
    }
  },
  computed: {
    accessTypeList () {
      return EnumDatasService.getAccessTypeList()
    },
    permissionClassList () {
      return EnumDatasService.getPermissionClassList()
    },
    subscribeTypeList () {
      return EnumDatasService.getSubscribeTypeList()
    },
    subjectTypeList () {
      return EnumDatasService.getSubjectTypeList()
    },
    displayOrderTypeList () {
      return EnumDatasService.getDisplayOrderTypeList().filter((element) => { return !CommonUtils.equals(element.name, 'CUSTOM') })
    },
    permissionTypeList () {
      return EnumDatasService.getPermissionTypeList()
    },
    userDisplayedAttrs () {
      return SubjectService.getUserDisplayedAttrs()
    },
    operatorTypeList () {
      return EnumDatasService.getOperatorTypeList()
    },
    stringEvaluationModeList () {
      return EnumDatasService.getStringEvaluationModeList()
    },
    itemStatusList () {
      return EnumDatasService.getItemStatusList()
    }
  },
  methods: {
    loadAll () {
      this.initSubjectInfosConfig()
      this.ctxType = this.$route.params.ctxType
      this.ctxId = this.$route.params.ctxId
      this.load(this.ctxType, this.ctxId)
    },
    load (ctxType, ctxId) {
      switch (ctxType) {
        case 'ORGANIZATION':
          OrganizationService.get(ctxId).then(result => {
            this.context = result.data
            this.publisher = {}
            this.ctxPermissionType = 'CONTEXT'
            this.getUserCanManage(result.data.contextKey)
            this.hasTargetManagment = this.getHasTargetManagment(ctxType)
            this.hasPermissionManagment = this.getHasPermissionManagment(ctxType)
            this.selectTemplatePermissionTemplate(this.ctxPermissionType)
          })
          PermissionOnContextService.queryForCtx(ctxType, ctxId).then(result => {
            this.permissions = result.data
            this.loadAvailableRoles()
          })
          SubscriberService.queryForCtx(ctxType, ctxId).then(result => {
            this.targets = result.data
          })
          break
        case 'PUBLISHER':
          PublisherService.get(ctxId).then(result => {
            this.context = result.data
            this.publisher = result.data
            this.getUserCanManage(result.data.contextKey)
            this.hasTargetManagment = this.getHasTargetManagment(ctxType)
            this.ctxPermissionType = this.publisher.permissionType
            this.hasPermissionManagment = this.getHasPermissionManagment(ctxType)
            this.selectTemplatePermissionTemplate(this.ctxPermissionType)
            this.setCanCreateCategory(this.publisher)
            this.selectPermissionManager(this.ctxPermissionType).queryForCtx(ctxType, ctxId).then(res => {
              this.permissions = res.data
              this.loadAvailableRoles()
            })
          })
          SubscriberService.queryForCtx(ctxType, ctxId).then(result => {
            this.targets = result.data
          })
          break
        case 'CATEGORY':
        case 'FEED':
          ClassificationService.get(ctxId).then(result => {
            this.context = result.data
            this.publisher = result.data.publisher
            this.getUserCanManage(result.data.contextKey)
            this.ctxPermissionType = this.publisher.permissionType
            this.hasTargetManagment = this.getHasTargetManagment(ctxType)
            this.hasPermissionManagment = this.getHasPermissionManagment(ctxType)
            this.selectTemplatePermissionTemplate(this.ctxPermissionType)
            this.selectPermissionManager(this.ctxPermissionType).queryForCtx(ctxType, ctxId).then(res => {
              this.permissions = res.data
              this.loadAvailableRoles()
            })
          })
          SubscriberService.queryForCtx(ctxType, ctxId).then(result => {
            this.targets = result.data
          })
          break
        case 'ITEM':
          ItemService.get(ctxId).then(result => {
            this.context = result.data
            this.getUserCanManage(result.data.contextKey)
            this.hasTargetManagment = this.getHasTargetManagment(ctxType)
            this.hasPermissionManagment = this.getHasPermissionManagment(ctxType)
          })
          this.permissions = {}
          SubscriberService.queryForCtx(ctxType, ctxId).then(result => {
            this.targets = result.data
          })
          break
      }
    },
    updateContext (callback) {
      let manager = null
      switch (this.ctxType) {
        case 'ORGANIZATION':
          manager = OrganizationService
          break
        case 'PUBLISHER':
          manager = PublisherService
          break
        case 'CATEGORY':
          manager = CategoryService
          break
        case 'FEED':
          manager = ClassificationService
          break
        case 'ITEM':
          break
        default:
          break
      }
      if (manager) {
        manager.get(this.context.id).then(res => {
          this.editedContext = res.data
          this.loadAvailableTypes(true)
          if (callback) {
            callback()
          }
        })
      }
    },
    confirmDeleteContext (callback) {
      let manager = null
      switch (this.ctxType) {
        case 'ORGANIZATION':
          manager = OrganizationService
          break
        case 'PUBLISHER':
          manager = PublisherService
          break
        case 'CATEGORY':
          manager = CategoryService
          break
        case 'FEED':
          manager = ClassificationService
          break
        case 'ITEM':
          manager = ContentService
          break
        default:
          throw new Error('not yet implemented')
      }
      if (manager) {
        manager.delete(this.context.contextKey.keyId).then(() => {
          if (callback) {
            callback()
          }
          if (this.parentNodeId) {
            const parentNodeId = this.parentNodeId
            this.deleteTreeNode(this.ctxId + ':' + this.ctxType)
            this.selectTreeNode(parentNodeId)
          } else {
            this.deleteTreeNode(this.ctxId + ':' + this.ctxType)
            this.$router.push({ name: 'Treeview' })
          }
        })
      }
    },
    confirmUpdateContext (type, callback) {
      let manager = null
      let text = null
      switch (type) {
        case 'ORGANIZATION':
          manager = OrganizationService
          text = this.editedContext.name
          this.editedContext.displayName = this.editedContext.name
          if (typeof this.editedContext.identifiers === 'string') {
            var identifiersValues = this.editedContext.identifiers
            this.editedContext.identifiers = []
            if (identifiersValues.includes(',')) {
              identifiersValues = identifiersValues.split(',')
              identifiersValues.forEach(identifiersValue => {
                this.editedContext.identifiers.push(identifiersValue)
              })
            } else {
              this.editedContext.identifiers.push(identifiersValues)
            }
          }
          break
        case 'PUBLISHER':
          manager = PublisherService
          text = this.editedContext.displayName
          break
        case 'CATEGORY':
          manager = CategoryService
          text = this.editedContext.name
          break
        case 'FEED':
          manager = ClassificationService
          text = this.editedContext.name
          break
        case 'ITEM':
          break
        default:
          break
      }

      if (manager) {
        manager.update(this.editedContext).then(() => {
          if (callback) {
            callback()
          }
          // verrue Flash
          // pour mettre à jour l'ordre de trie dans la rubrique cachée du Flash-info, en attendant une meilleure gestion sans rubrique cachée
          if (type === 'PUBLISHER' && this.editedContext.context.redactor.writingMode === 'STATIC' &&
            this.editedContext.context.reader.authorizedTypes.includes('FLASH')) {
            ClassificationService.query(this.editedContext.id, false).then(result => {
              result.data.forEach(obj => {
                obj.defaultDisplayOrder = this.editedContext.defaultDisplayOrder
                CategoryService.update(obj)
              })
            })
          }
          // Cas de création d'enfant, type d'objet créé différent de l'objet actuel, pas de mise à jour du texte du noeud courant
          if (this.ctxType !== type) {
            this.refreshTreeNode(this.ctxId + ':' + this.ctxType, {})
          } else {
            this.refreshTreeNode(this.ctxId + ':' + this.ctxType, { text: text })
          }
          this.load(this.ctxType, this.ctxId)
        })
      }
    },
    createContext (type, callback) {
      this.loadAvailableTypes(false)
      switch (type) {
        case 'ORGANIZATION':
          this.editedContext = {}
          break
        case 'PUBLISHER':
          this.editedContext = {}
          break
        case 'CATEGORY':
          // filter on creation of category on publisher flash context
          if (!(this.context.context.redactor.writingMode === 'STATIC' && this.context.context.reader.authorizedTypes.includes('FLASH'))) {
            // in waiting all access are PUBLIC
            const defaultColor = this.context.context.reader.classificationDecorations.includes('COLOR') ? this.paletteColorPicker[0] : null
            this.editedContext = {
              type: 'CATEGORY',
              publisher: this.context,
              rssAllowed: false,
              name: null,
              iconUrl: null,
              color: defaultColor,
              lang: this.langList[0].id,
              ttl: 3600,
              displayOrder: 0,
              accessView: 'PUBLIC',
              description: null,
              defaultDisplayOrder: this.autorizedDisplayOrderTypeList[0].name,
              createdBy: null,
              createdDate: null,
              lastModifiedBy: null,
              lastModifiedDate: null,
              id: null
            }
          }
          break
        case 'FEED':
          this.editedContext = {}
          break
        default:
          break
      }
      if (callback) {
        callback()
      }
    },
    loadAvailableTypes (isEditCurrentCtx) {
      var ctx = isEditCurrentCtx ? this.editedContext : this.context
      this.autorizedDisplayOrderTypeList = this.displayOrderTypeList
      this.autorizedPermissionClassList = this.permissionClassList
      switch (ctx.contextKey.keyType) {
        case 'ORGANIZATION':
          // in edit we are on ORGANIZATION else we create a sub context
          if (isEditCurrentCtx) {
            this.autorizedDisplayOrderTypeList = this.displayOrderTypeList.filter(element => element.name !== 'START_DATE')
          } else {
            throw new Error('not yet implemented')
          }
          break
        case 'PUBLISHER':
          // in edit we are on PUBLISHER else we create a sub context
          if (isEditCurrentCtx) {
            // If not Flash context, ie no Classification management we remove START_DATE order
            if (!(ctx.context.redactor.writingMode === 'STATIC' && ctx.context.reader.authorizedTypes.includes('FLASH'))) {
              this.autorizedDisplayOrderTypeList = this.displayOrderTypeList.filter(element => element.name !== 'START_DATE')
            } else {
              // we keep only "CONTEXT" in Flash context
              this.autorizedPermissionClassList = this.permissionClassList.filter(element => element.name === 'CONTEXT')
            }
          } else if (ctx.context.redactor.nbLevelsOfClassification > 1) {
            this.autorizedDisplayOrderTypeList = this.displayOrderTypeList.filter(element => element.name !== 'START_DATE')
          }
          break
        case 'CATEGORY':
          // in edit we are on CATEGORY else we create a sub context
          if (isEditCurrentCtx) {
            if (ctx.publisher.context.redactor.nbLevelsOfClassification > 1) {
              this.autorizedDisplayOrderTypeList = this.displayOrderTypeList.filter(element => element.name !== 'START_DATE')
            }
          } else {
            throw new Error('not yet implemented')
          }
          break
        default:
          break
      }
    },
    selectPermissionManager (permissionClass) {
      switch (permissionClass) {
        case 'CONTEXT':
          return PermissionOnContextService
        case 'CONTEXT_WITH_SUBJECTS':
          return PermissionOnClassificationWithSubjectListService
        default:
          throw new Error('not yet implemented')
      }
    },
    getUserCanManage (contextKey) {
      UserService.canEditCtx(contextKey.keyId, contextKey.keyType).then(result => {
        this.canManage = (result.data.value === true)
      })
    },
    selectTemplatePermissionTemplate (permissionClass) {
      switch (permissionClass) {
        case 'CONTEXT':
          this.permissionTemplate = 'permissionOnCtx'
          break
        case 'CONTEXT_WITH_SUBJECTS':
          this.permissionTemplate = 'permissionOnCtxWithSubjects'
          break
        default:
          throw new Error('not yet implemented')
      }
    },
    setCanCreateCategory (publisher) {
      if (publisher.context.redactor.writingMode === 'STATIC' &&
        publisher.context.reader.authorizedTypes.includes('FLASH')) {
        this.canCreateCategory = false
      } else {
        UserService.canCreateInCtx(this.context.contextKey.keyId, this.context.contextKey.keyType).then(response => {
          this.canCreateCategory = (response.data.value === true)
        })
        this.canCreateCategory = false
      }
    },
    getHasTargetManagment (ctxType) {
      switch (ctxType) {
        case 'ORGANIZATION':
          return PrincipalService.isInRole('ROLE_ADMIN')
        case 'PUBLISHER':
          return PrincipalService.isInRole('ROLE_ADMIN') && CommonUtils.equals(this.publisher.context.redactor.writingMode, 'STATIC') &&
                !this.inArray('FLASH', this.context.context.reader.authorizedTypes)
        case 'CATEGORY':
        case 'FEED':
          if (!CommonUtils.equals({}, this.publisher)) {
            return !CommonUtils.equals(this.publisher.context.redactor.writingMode, 'TARGETS_ON_ITEM') && this.publisher.hasSubPermsManagement
          }
          return false
        case 'ITEM':
          if (!CommonUtils.equals({}, this.context)) {
            return CommonUtils.equals(this.context.redactor.writingMode, 'TARGETS_ON_ITEM')
          }
          return false
        default:
          return false
      }
    },
    getHasPermissionManagment (ctxType) {
      switch (ctxType) {
        case 'ORGANIZATION':
          return PrincipalService.isInRole('ROLE_ADMIN')
        case 'PUBLISHER':
          return true
        case 'CATEGORY':
        case 'FEED':
          if (!CommonUtils.equals({}, this.publisher)) {
            return this.publisher.hasSubPermsManagement
          }
          return false
        case 'ITEM':
          return false
        default:
          return false
      }
    },
    inArray (item, array) {
      if (array === null || array === undefined || !CommonUtils.isArray(array) || array.length < 1) return false
      for (var i = 0, size = array.length; i < size; i++) {
        if (CommonUtils.equals(array[i], item)) {
          return true
        }
      }
      return false
    },
    getEnumlabel (type, name) {
      if (name) {
        try {
          switch (type) {
            case 'accessType':
              return this.accessTypeList.find(val => val.name === name).label
            case 'permissionClass':
              return this.permissionClassList.find(val => val.name === name).label
            case 'displayOrderType':
              return this.displayOrderTypeList.find(val => val.name === name).label
            case 'subjectType':
              return this.subjectTypeList.find(val => val.code === name).descKey
            case 'permissionType':
              return this.permissionTypeList.find(val => val.name === name).label
            case 'subscribeType':
              return this.subscribeTypeList.find(val => val.name === name).label
            case 'lang':
              return this.langList.find(val => val.id === name).label
            case 'itemStatus':
              return this.itemStatusList.find(val => val.name === name).label
          }
        } catch {
          // Fallback sur un label vide si propriété non trouvé dan les énums
          return ''
        }
      }
    },
    loadAvailableRoles (withRole) {
      this.availableRoles = []
      for (var i = 0; i < this.permissionTypeList.length; i++) {
        switch (this.ctxPermissionType) {
          case 'CONTEXT_WITH_SUBJECTS':
          case 'CONTEXT':
            var found = false
            for (var l = 0; l < this.permissions.length; l++) {
              if (CommonUtils.equals(this.permissions[l].role, this.permissionTypeList[i].name)) {
                found = true
              }
            }
            if ((!found && !CommonUtils.equals(this.permissionTypeList[i].name, 'ADMIN')) ||
            (CommonUtils.isDefined(withRole) && CommonUtils.equals(this.permissionTypeList[i].name, withRole))) {
              this.availableRoles.push(this.permissionTypeList[i])
            }
            break
          default:
            if (!CommonUtils.equals(this.permissionTypeList[i].name, 'ADMIN')) {
              this.availableRoles.push(this.permissionTypeList[i])
            }
            break
        }
      }
      if (!CommonUtils.equals(this.ctxType, 'ORGANIZATION')) {
        this.availableRoles = this.availableRoles.filter(function (element) {
          return !CommonUtils.equals(element.name, 'LOOKOVER')
        })
      }
    },
    // Configuration du webcomponent esup-subject-infos
    initSubjectInfosConfig () {
      this.subjectInfosConfig.userDisplayedAttrs = this.userDisplayedAttrs
      this.subjectInfosConfig.getSubjectInfos = (keyType, keyId) => {
        return SubjectService.getSubjectInfos(keyType, keyId).then(res => {
          if (res) {
            return res.data
          }
        })
      }
    },
    clear () {
      this.$ref.tabTargets.clearSubscriber()
    },
    getSubjectTypeList (name) {
      return this.subjectTypeList.find(val => val.code === name).id
    },
    reset () {
      this.loadAll()
    },
    showNav (div) {
      this.activeNav = div
    }
  },
  mounted () {
    this.loadAll()
  }
}
</script>
