<template>
  <div>
    <h3 class="mt-3 mb-2">{{ $t("manager.treeview.details.context.properties") }}</h3>
    <div class="mb-3">
      <button type="button" v-can-edit="context.contextKey"
        @click="updatePublisher()"
        class="btn btn-primary me-1">
        <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
      </button>
      <button type="button" v-has-role="'ROLE_ADMIN'"
        @click="deletePublisher()"
        class="btn btn-danger me-1">
        <span class="far fa-times-circle"></span>&nbsp;<span>{{$t("entity.action.delete")}}</span>
      </button>
      <button type="button" v-if="canCreateCategory" v-can-create-in="context.contextKey"
        @click="createCategory()"
        class="btn btn-primary">
        <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t('category.home.createLabel')}}</span>
      </button>
    </div>

    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5"><span>{{$t('publisher.context.key')}}</span></dt>
      <dd class="col-sm-7"><span>{{contextName}}</span></dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5"><span>{{$t('publisher.displayName')}}</span></dt>
      <dd class="col-sm-7"><span>{{context.displayName}}</span></dd>
    </dl>
    <dl class="row entity-details" v-if="canCreateCategory">
      <dt class="col-sm-5"><span>{{$t('publisher.permissionType')}}</span></dt>
      <dd class="col-sm-7"><span>{{$t(getPermissionClassLabel(context.permissionType))}}</span></dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5"><span>{{$t('publisher.used')}}</span></dt>
      <dd class="col-sm-7"><span><input type="checkbox" class="input-sm" v-model="context.used" disabled></span></dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5"><span>{{$t('publisher.defaultDisplayOrder')}}</span></dt>
      <dd class="col-sm-7"><span>{{$t(getDisplayOrderTypeLabel(context.defaultDisplayOrder))}}</span></dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5"><span>{{$t('publisher.displayOrder')}}</span></dt>
      <dd class="col-sm-7"><span>{{context.displayOrder}}</span></dd>
    </dl>
    <dl class="row entity-details" v-if="canCreateCategory">
      <dt class="col-sm-5"><span>{{$t('publisher.hasSubPermsManagement')}}</span></dt>
      <dd class="col-sm-7"><span><input type="checkbox" class="input-sm" v-model="context.hasSubPermsManagement" disabled></span></dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5"><span>{{$t('publisher.doHighlight')}}</span></dt>
      <dd class="col-sm-7"><span><input type="checkbox" class="input-sm" v-model="context.doHighlight" disabled></span></dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5"><span><i class="fa fa-rss fa-lg"></i></span></dt>
      <dd class="col-sm-7"><span><a :href="appUrl + 'feed/rss/' + (context.context ? context.context.organization.id : '') + '?pid=' + context.id" target="_blank">{{appUrl}}feed/rss/{{context.context ? context.context.organization.id : ''}}?pid={{context.id}}</a></span></dd>
    </dl>

    <div class="modal fade" id="savePublisherModal" tabindex="-1" role="dialog" aria-labelledby="myPublisherLabel"
      aria-hidden="true" ref="savePublisherModal">
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 class="modal-title" id="myPublisherLabel">{{$t('publisher.home.createOrEditLabel')}}</h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <div class="form-group" v-has-role="'ROLE_ADMIN'">
                <label class="control-label" for="ID" >ID</label>
                <input type="text" class="form-control" name="id" id="ID" v-model="editedContext.id" disabled>
              </div>
              <div class="form-group" v-has-role="'ROLE_ADMIN'">
                <label class="control-label" for="context">{{$t('publisher.context.key')}}</label>
                <input type="text" class="form-control" name="context" id="context"
                :value="editedContext.context ? editedContext.context.reader.displayName + ' - ' + editedContext.context.redactor.displayName + ' - ' + editedContext.context.organization.name : ''" disabled>
              </div>
              <div class="form-group">
                <label for ="displayName" class="control-label">{{$t('publisher.displayName')}}</label>
                <input type="text" class="form-control" name="displayName" id="displayName"
                  v-model="editedContextDisplayName" required minlength="3" maxlength="50" :disabled="!isInRole('ROLE_ADMIN')">
                <div class="invalid-feedback"
                  v-if="formValidator.hasError('displayName', formErrors.REQUIRED)">
                  {{$t('entity.validation.required')}}
                </div>
                <div class="invalid-feedback"
                  v-if="formValidator.hasError('displayName', formErrors.MIN_LENGTH)">
                  {{$t('entity.validation.minlength', {min:'3'})}}
                </div>
                <div class="invalid-feedback"
                  v-if="formValidator.hasError('displayName', formErrors.MAX_LENGTH)">
                  {{$t('entity.validation.maxlength', {max:'50'})}}
                </div>
              </div>
              <div class="form-group" v-if="canCreateCategory">
                <label class="control-label" for="permissionType" >{{$t('publisher.permissionType')}}</label>
                <select class="form-select" id="permissionType" name="permissionType" v-model="editedContextPermissionType" required :disabled="!canManage">
                  <option v-for="permission in permissionClassList" :key="permission.id" :value="permission.name">
                    {{$t(permission.label)}}
                  </option>
                </select>
                <div class="invalid-feedback"
                  v-if="formValidator.hasError('permissionType', formErrors.REQUIRED)">
                  {{$t('entity.validation.required')}}
                </div>
              </div>
              <div class="form-group">
                <label for="used" class="control-label me-2">{{$t('publisher.used')}}</label>
                <input type="checkbox" class="form-check-input " name="used" id="used" v-model="editedContextUsed" :disabled="!canManage">
              </div>
                <div class="form-group">
                  <label class="control-label" for="defaultDisplayOrder" >{{$t('publisher.defaultDisplayOrder')}}</label>
                  <select class="form-select" id="defaultDisplayOrder" name="defaultDisplayOrder" v-model="editedContextDefaultDisplayOrder" required :disabled="!canManage">
                    <option v-for="displayOrderType in autorizedDisplayOrderTypeList" :key="displayOrderType.id" :value="displayOrderType.name">
                      {{$t(displayOrderType.label)}}
                    </option>
                  </select>
                  <div class="invalid-feedback"
                    v-if="formValidator.hasError('defaultDisplayOrder', formErrors.REQUIRED)">
                    {{$t('entity.validation.required')}}
                  </div>
                </div>
                <div class="form-group" v-has-role="'ROLE_ADMIN'">
                  <label for="displayOrder" class="control-label">{{$t('publisher.displayOrder')}}</label>
                  <input type="number" class="form-control" name="displayOrder" id="displayOrder"
                    v-model="editedContextDisplayOrder" required min="0" max="9" :disabled="!isInRole('ROLE_ADMIN')">
                  <div class="invalid-feedback"
                    v-if="formValidator.hasError('displayOrder', formErrors.REQUIRED)">
                    {{$t('entity.validation.required')}}
                  </div>
                  <div class="invalid-feedback"
                    v-if="formValidator.hasError('displayOrder', formErrors.MIN_VALUE)">
                    {{$t('entity.validation.min', {min:'0'})}}
                  </div>
                  <div class="invalid-feedback"
                    v-if="formValidator.hasError('displayOrder', formErrors.MAX_VALUE)">
                    {{$t('entity.validation.max', {max:'9'})}}
                  </div>
                </div>
                <div class="form-group" v-if="canCreateCategory">
                  <label for="hasSubPermsManagement" class="control-label me-2">{{$t('publisher.hasSubPermsManagement')}}</label>
                  <input type="checkbox" class="form-check-input " name="hasSubPermsManagement" id="hasSubPermsManagement"
                    v-model="editedContextHasSubPermsManagement" disabled>
                </div>
                <div class="form-group" v-has-role="'ROLE_ADMIN'">
                  <label for="doHighlight" class="control-label me-2">{{$t('publisher.doHighlight')}}</label>
                  <input type="checkbox" class="form-check-input " name="doHighlight" id="doHighlight"
                    v-model="editedContextDoHighlight" :disabled="!isInRole('ROLE_ADMIN')">
                </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
              </button>
              <button type="button" class="btn btn-primary" :disabled="formValidator.hasError() || !canManage" @click="confirmUpdate">
                <span class="fas fa-download"></span>&nbsp;<span>{{$t('entity.action.save')}}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div class="modal fade" ref="deletePublisherConfirmation">
      <div class="modal-dialog">
        <div class="modal-content">
          <form name="deleteForm">
            <div class="modal-header">
              <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <p>{{$t('publisher.delete.question', {id: contextName})}}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete()">
                <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <CategoryForm ref="categoryForm"></CategoryForm>
  </div>
</template>

<script>
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import PrincipalService from '@/services/auth/PrincipalService'
import { FormValidationUtils, FormErrorType } from '@/services/util/FormValidationUtils'
import { Modal } from 'bootstrap'
import CategoryForm from './CategoryForm.vue'

export default {
  name: 'Publisher',
  components: {
    CategoryForm
  },
  data () {
    return {
      deleteModal: null,
      updateModal: null,
      categoryForm: null,
      appUrl: window.location.origin + process.env.VUE_APP_BACK_BASE_URL,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType
    }
  },
  inject: [
    'context', 'editedContext', 'setEditedContext', 'updateContext', 'confirmDeleteContext', 'confirmUpdateContext',
    'canCreateCategory', 'canManage', 'autorizedDisplayOrderTypeList', 'getEnumlabel', 'createContext'
  ],
  computed: {
    editedContextDisplayName: {
      get () {
        return this.editedContext.displayName || ''
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.displayName = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    editedContextPermissionType: {
      get () {
        return this.editedContext.permissionType || ''
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.permissionType = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    editedContextUsed: {
      get () {
        return this.editedContext.used || false
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.used = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    editedContextDefaultDisplayOrder: {
      get () {
        return this.editedContext.defaultDisplayOrder || ''
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.defaultDisplayOrder = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    editedContextDisplayOrder: {
      get () {
        return this.editedContext.displayOrder || 0
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.displayOrder = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    editedContextHasSubPermsManagement: {
      get () {
        return this.editedContext.hasSubPermsManagement || false
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.hasSubPermsManagement = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    editedContextDoHighlight: {
      get () {
        return this.editedContext.doHighlight || false
      },
      set (newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.doHighlight = newVal
        this.setEditedContext(newEditedContext)
      }
    },
    // Liste des types de permission
    permissionClassList () {
      return EnumDatasService.getPermissionClassList()
    },
    // Nom du contexte du publisher
    contextName () {
      return this.context && this.context.context ? this.context.context.reader.displayName + ' - ' + this.context.context.redactor.displayName + ' - ' + this.context.context.organization.name : ''
    }
  },
  methods: {
    isInRole (role) {
      return PrincipalService.isInRole(role)
    },
    initFormValidator () {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('displayName', this.editedContext.displayName, 3, 50, true)
      this.formValidator.checkTextFieldValidity('permissionType', this.editedContext.permissionType, null, null, true)
      this.formValidator.checkTextFieldValidity('defaultDisplayOrder', this.editedContext.defaultDisplayOrder, null, null, true)
      this.formValidator.checkNumberFieldValidity('displayOrder', this.editedContext.displayOrder, 0, 9, true)
    },
    // Méthode de création et de mise à jour de contexte de publication
    confirmUpdate () {
      this.confirmUpdateContext('PUBLISHER', () => {
        this.updateModal.hide()
      })
    },
    createCategory () {
      this.createContext('CATEGORY', () => {
        this.categoryForm.show()
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de contexte de publication
    updatePublisher () {
      this.updateContext(() => {
        this.initFormValidator()
        this.updateModal.show()
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de contexte de publication
    deletePublisher () {
      this.deleteModal.show()
    },
    // Méthode en charge de supprimer un contexte de publication
    confirmDelete () {
      this.confirmDeleteContext(() => {
        this.deleteModal.hide()
      })
    },
    getDisplayOrderTypeLabel (name) {
      return this.getEnumlabel('displayOrderType', name) || ''
    },
    getPermissionClassLabel (name) {
      return this.getEnumlabel('permissionClass', name) || ''
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deletePublisherConfirmation)
    this.updateModal = new Modal(this.$refs.savePublisherModal)
    this.categoryForm = this.$refs.categoryForm
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'editedContext.displayName' (newVal) {
      this.formValidator.checkTextFieldValidity('displayName', newVal, 3, 50, true)
    },
    'editedContext.permissionType' (newVal) {
      this.formValidator.checkTextFieldValidity('permissionType', newVal, null, null, true)
    },
    'editedContext.defaultDisplayOrder' (newVal) {
      this.formValidator.checkTextFieldValidity('defaultDisplayOrder', newVal, null, null, true)
    },
    'editedContext.displayOrder' (newVal) {
      this.formValidator.checkNumberFieldValidity('displayOrder', newVal, 0, 9, true)
    }
  }
}
</script>
