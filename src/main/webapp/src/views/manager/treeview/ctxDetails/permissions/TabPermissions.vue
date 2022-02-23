<template>
  <div v-if="permissionTemplate === 'permissionOnCtx'">
    <PermissionOnCtx></PermissionOnCtx>
  </div>
  <div v-else-if="permissionTemplate === 'permissionOnCtxWithSubjects'">
    <PermissionOnCtxWithSubjects></PermissionOnCtxWithSubjects>
  </div>

  <div class="modal fade" id="deletePermissionConfirmation" ref="deletePermissionConfirmation">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="deleteForm" >
          <div class="modal-header">
            <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clearPermission"></button>
          </div>
          <div class="modal-body">
            <p>{{$t('permission.delete.question', {id: $t(getPermissionTypeLabel(permission.role))})}}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clearPermission">
              <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmDeletePermission(permission.id)">
                <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import PermissionOnCtxWithSubjects from './PermissionOnCtxWithSubjects'
import PermissionOnCtx from './PermissionOnCtx'
import PermissionService from '@/services/entities/permission/PermissionService'
import PermissionOnContextService from '@/services/entities/permissionOnContext/PermissionOnContextService'
import SubjectService from '@/services/params/SubjectService'
import CommonUtils from '@/services/util/CommonUtils'
import i18n from '@/i18n'
import { computed, readonly } from 'vue'
import { Modal } from 'bootstrap'

const { t } = i18n.global

export default {
  name: 'TabPermissions',
  components: {
    PermissionOnCtx,
    PermissionOnCtxWithSubjects
  },
  data () {
    return {
      addPerm: true,
      deletePermissionConfirmation: null,
      selectedSubjects: [],
      ctxType: this.$route.params.ctxType,
      ctxId: this.$route.params.ctxId,
      contextPermission: { keyId: null, keyType: null },
      editEvaluatorError: false,
      permission: { type: '', context: { keyId: null, keyType: null }, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null, evaluator: { class: null, id: null, type: null, evaluators: [] }, authorizedSubjects: [] },
      permissionAdvanced: false,
      isDatasLoad: false
    }
  },
  provide () {
    return {
      addPerm: readonly(computed(() => this.addPerm)),
      editEvaluatorError: readonly(computed(() => this.editEvaluatorError)),
      selectedSubjects: readonly(computed(() => this.selectedSubjects)),
      permission: computed(() => this.permission),
      permissionAdvanced: readonly(computed(() => this.permissionAdvanced)),
      isDatasLoad: readonly(computed(() => this.isDatasLoad)),
      setIsDatasLoad: (isDatasLoad) => { this.isDatasLoad = isDatasLoad },
      addPermission: this.addPermission,
      areSubjectsSelected: this.areSubjectsSelected,
      changePermissionAdvanced: this.changePermissionAdvanced,
      clearPermission: this.clearPermission,
      createPermission: this.createPermission,
      deletePermission: this.deletePermission,
      getPermissionTypeLabel: this.getPermissionTypeLabel,
      onModificationEditEvaluator: this.onModificationEditEvaluator,
      isAdvancedEvaluator: this.isAdvancedEvaluator,
      updatePermission: this.updatePermission,
      updateSelectedSubject: this.updateSelectedSubject,
      updateAuthorizedSubjects: this.updateAuthorizedSubjects,
      removeFromSelectedSubjects: this.removeFromSelectedSubjects,
      removePermTarget: this.removePermTarget,
      userDisplayedAttrs: this.userDisplayedAttrs,
      userFonctionalAttrs: this.userFonctionalAttrs
    }
  },
  inject: [
    'load', 'context', 'ctxPermissionType', 'loadAvailableRoles', 'availableRoles', 'permissionTypeList',
    'selectPermissionManager', 'setCtxPermissionType', 'setPermissions', 'permissionTemplate'
  ],
  computed: {
    userDisplayedAttrs () {
      return SubjectService.getUserDisplayedAttrs()
    },
    userFonctionalAttrs () {
      return SubjectService.getUserFonctionalAttrs()
    }
  },
  methods: {
    addPermission () {
      this.addPerm = false
      this.loadAvailableRoles()
      this.selectedSubjects = []
      switch (this.ctxPermissionType) {
        case 'CONTEXT':
          this.permission = {
            type: 'PERMONCTX',
            context: { keyId: this.context.contextKey.keyId, keyType: this.context.contextKey.keyType },
            role: this.availableRoles[0].name,
            createdBy: null,
            createdDate: null,
            lastModifiedBy: null,
            lastModifiedDate: null,
            id: null,
            evaluator: { class: 'OPERATOR', id: null, type: 'OR', evaluators: [] }
          }
          this.editEvaluatorError = true
          break
        case 'CONTEXT_WITH_SUBJECTS':
          this.permission = {
            type: 'PERMONCTXWSUBJS',
            context: { keyId: this.context.contextKey.keyId, keyType: this.context.contextKey.keyType },
            role: this.availableRoles[0].name,
            createdBy: null,
            createdDate: null,
            lastModifiedBy: null,
            lastModifiedDate: null,
            id: null,
            evaluator: { class: 'OPERATOR', id: null, type: 'OR', evaluators: [] },
            authorizedSubjects: []
          }
          this.editEvaluatorError = true
          break
        default :
          this.permission = {}
          break
      }
    },
    areSubjectsSelected () {
      return this.selectedSubjects.length > 0
    },
    changePermissionAdvanced () {
      this.permissionAdvanced = !this.permissionAdvanced
    },
    clearPermission () {
      this.permission = { type: '', context: { keyId: null, keyType: null }, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null, evaluator: { class: null, id: null, type: null, evaluators: [] } }
      this.deletePermissionConfirmation.hide()
      this.loadAvailableRoles()
      this.editEvaluatorError = false
      this.addPerm = true
    },
    confirmDeletePermission (id) {
      PermissionService.delete(id).then(() => {
        this.load(this.ctxType, this.context.id)
        this.deletePermissionConfirmation.hide()
        this.permission = { type: '', context: { keyId: null, keyType: null }, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null, evaluator: { class: null, id: null, type: null, evaluators: [] } }
      })
    },
    createPermission () {
      if (!this.permissionAdvanced) {
        this.permission.evaluator.evaluators = []
        for (var i = 0; i < this.selectedSubjects.length; i++) {
          if (this.selectedSubjects[i].keyType === 'PERSON' || (this.selectedSubjects[i].modelId && this.selectedSubjects[i].modelId.keyType === 'PERSON')) {
            this.permission.evaluator.evaluators.push({ class: 'USERATTRIBUTES', id: null, mode: 'EQUALS', attribute: 'uid', value: this.selectedSubjects[i].modelId ? this.selectedSubjects[i].modelId.keyId : this.selectedSubjects[i].keyId })
          } else if (this.selectedSubjects[i].keyType === 'GROUP' || (this.selectedSubjects[i].modelId && this.selectedSubjects[i].modelId.keyType === 'GROUP')) {
            this.permission.evaluator.evaluators.push({ class: 'USERGROUP', id: null, group: this.selectedSubjects[i].modelId ? this.selectedSubjects[i].modelId.keyId : this.selectedSubjects[i].keyId })
          }
        }
      }
      this.selectPermissionManager(this.ctxPermissionType).update(this.permission).then(() => {
        this.load(this.ctxType, this.context.id)
        this.clearPermission()
      })
    },
    deletePermission (id) {
      PermissionService.get(id).then(result => {
        this.permission = result.data
        this.deletePermissionConfirmation.show()
      })
    },
    getEnumlabel (name) {
      var data
      data = this.permissionTypeList.find(val => {
        return val.name === name
      })
      return data ? data.label : ''
    },
    getPermissionTypeLabel (name) {
      return this.getEnumlabel(name)
    },
    onModificationEditEvaluator (data, isValid) {
      this.permission.evaluator = data
      this.editEvaluatorError = !isValid
    },
    isAdvancedEvaluator (evaluator, depth) {
      depth = (typeof depth === 'undefined') ? 0 : depth
      if (depth < 2 && CommonUtils.isDefined(evaluator) && CommonUtils.isDefined(evaluator.class)) {
        switch (evaluator.class) {
          case 'OPERATOR':
            if (CommonUtils.isDefined(evaluator.type) && CommonUtils.equals('OR', evaluator.type) && CommonUtils.isDefined(evaluator.evaluators)) {
              var boolEval = false
              for (var i = 0; i < evaluator.evaluators.length; i++) {
                boolEval = boolEval && this.isAdvancedEvaluator(evaluator.evaluators[i], depth + 1)
                if (boolEval) return true
              }
              return false
            }
            return true
          case 'USERATTRIBUTES':
          case 'USERMULTIVALUEDATTRIBUTES':
          case 'USERGROUP':
            return false
          default:
            return true
        }
      }
      return true
    },
    removeFromSelectedSubjects (subject) {
      this.selectedSubjects = this.selectedSubjects.filter(function (element) {
        if ('modelId' in subject) {
          return !CommonUtils.equals(element.modelId, subject.modelId)
        } else {
          return !CommonUtils.equals(element, subject)
        }
      })
    },
    removePermTarget (subject) {
      this.permission.authorizedSubjects = this.permission.authorizedSubjects.filter(function (element) {
        if ('modelId' in subject) return !CommonUtils.equals(element, subject.modelId)
        return !CommonUtils.equals(element, subject)
      })
    },
    setSelectedSubjectsFromEvaluator (evaluator) {
      this.selectedSubjects = []
      if (!this.isAdvancedEvaluator(evaluator)) {
        for (var i = 0; i < evaluator.evaluators.length; i++) {
          var subjectEval = evaluator.evaluators[i]
          switch (subjectEval.class) {
            case 'USERATTRIBUTES':
            case 'USERMULTIVALUEDATTRIBUTES':
              if (subjectEval.attribute === 'uid' && subjectEval.mode === 'EQUALS') {
                this.selectedSubjects.push({ keyType: 'PERSON', keyId: subjectEval.value })
              }
              break
            case 'USERGROUP':
              this.selectedSubjects.push({ keyType: 'GROUP', keyId: subjectEval.group })
              break
            default : throw new Error('not yet implemented')
          }
        }
      }
    },
    updateAuthorizedSubjects (datas) {
      var newVal = datas[0]
      if (CommonUtils.isDefined(newVal) && !CommonUtils.equals({}, newVal) && !CommonUtils.equals(newVal, this.permission.authorizedSubjects)) {
        var found = false
        this.permission.authorizedSubjects.forEach(value => {
          if (CommonUtils.equals(newVal.modelId, value)) {
            found = true
          }
        })
        if (!found) {
          this.permission.authorizedSubjects.push(newVal.modelId)
        } else {
          this.$toast.warning(t('permission.subjectAlreadySelected'))
        }
      }
    },
    updatePermission (id) {
      PermissionOnContextService.get(id).then(result => {
        this.permission = result.data
        this.loadAvailableRoles(result.data.role)
        this.setSelectedSubjectsFromEvaluator(this.permission.evaluator)
        this.editEvaluatorError = false
        this.addPerm = false
      })
    },
    // Mise à jour du sujet séléctionné
    updateSelectedSubject (datas) {
      var newVal = datas[0]
      if (CommonUtils.isDefined(newVal) && !CommonUtils.equals({}, newVal) && !CommonUtils.equals(newVal, this.selectedSubjects)) {
        var found = false
        this.selectedSubjects.forEach(subject => {
          if (CommonUtils.equals(newVal.modelId, subject)) {
            found = true
          }
        })
        if (!found) {
          this.selectedSubjects.push(newVal)
        } else {
          this.$toast.warning(t('permission.subjectAlreadySelected'))
        }
      }
    }
  },
  mounted () {
    this.deletePermissionConfirmation = new Modal(this.$refs.deletePermissionConfirmation)
  }
}
</script>
