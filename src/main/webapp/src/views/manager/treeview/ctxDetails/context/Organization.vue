<template>
  <div>
    <h3 class="mt-3 mb-2">
      {{ $t('manager.treeview.details.context.properties') }}
    </h3>
    <div class="mb-3">
      <button type="button" @click="updateOrganization()" class="btn btn-primary btn-sm me-1" v-has-role="'ROLE_ADMIN'">
        <span class="fas fa-pencil"></span>&nbsp;<span>{{ $t('entity.action.edit') }}</span>
      </button>
      <button type="button" @click="deleteOrganization()" class="btn btn-danger btn-sm me-1" v-has-role="'ROLE_ADMIN'">
        <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
      </button>
    </div>

    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('organization.name') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.name }}</span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('organization.description') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.description }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t('organization.displayOrder') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.displayOrder }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t('organization.allowNotifications') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span><input type="checkbox" v-model="context.allowNotifications" disabled /></span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5"><i class="fa fa-rss fa-lg"></i></dt>
      <dd class="col-sm-7">
        <a :href="appUrl + 'feed/rss/' + context.id" target="_blank">{{ appUrl }}feed/rss/{{ context.id }}</a>
      </dd>
    </dl>

    <div
      class="modal fade"
      id="saveOrganizationModal"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myOrganizationLabel"
      aria-hidden="true"
      ref="saveOrganizationModal"
    >
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 class="modal-title" id="myOrganizationLabel">
                {{ $t('organization.home.editLabel') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <div class="form-group" v-has-role="'ROLE_ADMIN'">
                <label class="control-label" for="ID">ID</label>
                <input type="text" class="form-control" name="id" id="ID" v-model="editedContext.id" disabled />
              </div>
              <div class="form-group">
                <label for="name" class="control-label">{{ $t('organization.name') }}</label>
                <input
                  type="text"
                  class="form-control"
                  name="name"
                  id="name"
                  v-model="editedContextName"
                  required
                  minlength="5"
                  maxlength="255"
                  :disabled="!isInRole('ROLE_ADMIN')"
                />
                <div class="invalid-feedback" v-if="formValidator.hasError('name', formErrors.REQUIRED)">
                  {{ $t('entity.validation.required') }}
                </div>
                <div class="invalid-feedback" v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)">
                  {{ $t('entity.validation.minlength', { min: '5' }) }}
                </div>
                <div class="invalid-feedback" v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)">
                  {{ $t('entity.validation.maxlength', { max: '255' }) }}
                </div>
              </div>
              <div class="form-group">
                <label for="description" class="control-label">{{ $t('organization.description') }}</label>
                <input
                  type="text"
                  class="form-control"
                  name="description"
                  id="description"
                  v-model="editedContextDescription"
                  required
                  minlength="5"
                  maxlength="512"
                  :disabled="!isInRole('ROLE_ADMIN')"
                />
                <div class="invalid-feedback" v-if="formValidator.hasError('description', formErrors.REQUIRED)">
                  {{ $t('entity.validation.required') }}
                </div>
                <div class="invalid-feedback" v-if="formValidator.hasError('description', formErrors.MIN_LENGTH)">
                  {{ $t('entity.validation.minlength', { min: '5' }) }}
                </div>
                <div class="invalid-feedback" v-if="formValidator.hasError('description', formErrors.MAX_LENGTH)">
                  {{ $t('entity.validation.maxlength', { max: '512' }) }}
                </div>
              </div>
              <div class="form-group" v-has-role="'ROLE_ADMIN'">
                <label for="identifiers" class="control-label">{{ $t('organization.identifiers') }}</label>
                <input
                  type="text"
                  id="identifiers"
                  name="identifiers"
                  class="form-control"
                  v-model="editedContextIdentifiers"
                  :placeholder="$t('organization.identifiers-help')"
                  required
                  :disabled="!isInRole('ROLE_ADMIN')"
                />
                <div class="invalid-feedback" v-if="formValidator.hasError('identifiers', formErrors.REQUIRED)">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div class="form-group" v-has-role="'ROLE_ADMIN'">
                <label for="displayOrder" class="control-label">{{ $t('organization.displayOrder') }}</label>
                <input
                  type="number"
                  class="form-control"
                  name="displayOrder"
                  id="displayOrder"
                  v-model="editedContextDisplayOrder"
                  required
                  min="0"
                  max="999"
                  :disabled="!isInRole('ROLE_ADMIN')"
                />
                <div class="invalid-feedback" v-if="formValidator.hasError('displayOrder', formErrors.REQUIRED)">
                  {{ $t('entity.validation.required') }}
                </div>
                <div class="invalid-feedback" v-if="formValidator.hasError('displayOrder', formErrors.MIN_VALUE)">
                  {{ $t('entity.validation.min', { min: '0' }) }}
                </div>
                <div class="invalid-feedback" v-if="formValidator.hasError('displayOrder', formErrors.MAX_VALUE)">
                  {{ $t('entity.validation.max', { max: '999' }) }}
                </div>
              </div>
              <div class="form-group" v-has-role="'ROLE_ADMIN'">
                <label for="allowNotifications" class="control-label">{{ $t('organization.allowNotifications') }}</label>
                <input
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="allowNotifications"
                  id="allowNotifications"
                  v-model="editedContextAllowNotifications"
                  :disabled="!canManage"
                />
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-primary" :disabled="formValidator.hasError() || !canManage" @click="confirmUpdate">
                <span class="fas fa-floppy-disk"></span>&nbsp;<span>{{ $t('entity.action.save') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div class="modal fade" ref="deleteOrganizationConfirmation">
      <div class="modal-dialog">
        <div class="modal-content">
          <form name="deleteForm">
            <div class="modal-header">
              <h4 class="modal-title">{{ $t('entity.delete.title') }}</h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <p>
                {{ $t('organization.delete.question', { id: context.name }) }}
              </p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete()">
                <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { FormValidationUtils, FormErrorType } from '@/services/util/FormValidationUtils';
import PrincipalService from '@/services/auth/PrincipalService';
import { Modal } from 'bootstrap';

export default {
  name: 'Organization',
  data() {
    return {
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    };
  },
  inject: [
    'context',
    'editedContext',
    'setEditedContext',
    'updateContext',
    'confirmDeleteContext',
    'confirmUpdateContext',
    'canManage',
    'appUrl',
  ],
  computed: {
    editedContextName: {
      get() {
        return this.editedContext.name || '';
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.name = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextDescription: {
      get() {
        return this.editedContext.description || '';
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.description = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextIdentifiers: {
      get() {
        return this.editedContext.identifiers || '';
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.identifiers = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextDisplayOrder: {
      get() {
        return this.editedContext.displayOrder || 0;
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.displayOrder = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextAllowNotifications: {
      get() {
        return this.editedContext.allowNotifications || false;
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.allowNotifications = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
  },
  methods: {
    isInRole(role) {
      return PrincipalService.isInRole(role);
    },
    // Méthode de création et de mise à jour de structure
    confirmUpdate() {
      this.confirmUpdateContext('ORGANIZATION', () => {
        this.updateModal.hide();
      });
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de structure
    updateOrganization() {
      this.updateContext(() => {
        this.initFormValidator();
        this.updateModal.show();
      });
    },
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear();
      this.formValidator.checkTextFieldValidity('name', this.editedContext.name, 5, 255, true);
      this.formValidator.checkTextFieldValidity('description', this.editedContext.description, 5, 512, true);
      this.formValidator.checkTextFieldValidity('identifiers', this.editedContext.identifiers, null, null, true);
      this.formValidator.checkNumberFieldValidity('displayOrder', this.editedContext.displayOrder, 0, 999, true);
    },
    // Méthode en charge d'ouvrir la modale de suppression de structure
    deleteOrganization() {
      this.deleteModal.show();
    },
    // Méthode en charge de supprimer une structure
    confirmDelete() {
      this.confirmDeleteContext(() => {
        this.deleteModal.hide();
      });
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteOrganizationConfirmation);
    this.updateModal = new Modal(this.$refs.saveOrganizationModal);
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'editedContext.name'(newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 5, 255, true);
    },
    'editedContext.description'(newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 5, 512, true);
    },
    'editedContext.identifiers'(newVal) {
      this.formValidator.checkTextFieldValidity('identifiers', newVal, null, null, true);
    },
    'editedContext.displayOrder'(newVal) {
      this.formValidator.checkNumberFieldValidity('displayOrder', newVal, 0, 999, true);
    },
  },
};
</script>
