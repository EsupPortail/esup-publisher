<template>
<div class='organization'>
    <h2 >{{$t('organization.home.title')}}</h2>
    <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#saveOrganizationModal" @click="clear" v-has-any-role="'ROLE_ADMIN'">
        <span class="fas fa-bolt"></span> <span >{{$t('organization.home.createLabel')}}</span>
    </button>
    <div class="modal fade" id="saveOrganizationModal" tabindex="-1" role="dialog" aria-labelledby="myOrganizationLabel"
         aria-hidden="true" ref="saveOrganizationModal">
        <div class="modal-dialog modal-fullscreen-md-down modal-lg">
            <div class="modal-content">
                <form name="editForm" role="form" novalidate show-validation>
                    <div class="modal-header">
                        <h4 class="modal-title" id="myOrganizationLabel">{{$t('organization.home.createOrEditLabel')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="control-label" for="ID" >ID</label>
                            <input type="text" class="form-control" name="id" id="ID"
                                   v-model="organization.id" disabled>
                        </div>

                        <div class="form-group">
                            <label for ="name" class="control-label">{{$t('organization.name')}}</label>
                            <input type="text" class="form-control" :class="(nameMinLength || nameMaxLength) ? 'is-invalid' : 'valid'" name="name" id="name"
                                   v-model="organization.name">

                            <div>
                                <p class="help-block"
                                   v-if="nameFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="nameMinLength">
                                    {{$t('entity.validation.minlength', {min:'5'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nameMaxLength" >
                                    {{$t('entity.validation.maxlength', {max:'255'})}}
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="description" class="control-label">{{$t('organization.description')}}</label>
                            <input type="text" class="form-control" :class="(descriptionMinLength || descriptionMaxLength) ? 'is-invalid' : 'valid'" name="description" id="description"
                                   v-model="organization.description" required>

                            <div>
                                <p class="help-block"
                                   v-if="descriptionFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="descriptionMinLength">
                                    {{$t('entity.validation.minlength', {min:'5'})}}
                                </p>
                                <p class="help-block"
                                   v-if="descriptionMaxLength">
                                    {{$t('entity.validation.maxlength', {max:'512'})}}
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="identifiers" class="control-label">{{$t('organization.identifiers')}}</label>
                            <input type="text" id="identifiers" name="identifiers" class="form-control" v-model="organization.identifiers"
                                   :placeholder="$t('organization.identifiers-help')" required>

                            <div>
                                <p class="help-block"
                                   v-if="identifiersFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="displayOrder" class="control-label">{{$t('organization.displayOrder')}}</label>
                            <input type="number" class="form-control" :class="(displayOrderMinLength || displayOrderMaxLength) ? 'is-invalid' : 'valid'" name="displayOrder" id="displayOrder"
                                   v-model="organization.displayOrder" required min="0" max="999">

                            <div>
                                <p class="help-block"
                                   v-if="displayOrderMinLength">
                                    {{$t('entity.validation.min', {min:'0'})}}
                                </p>
                                <p class="help-block"
                                   v-if="displayOrderMaxLength">
                                    {{$t('entity.validation.max', {max:'999'})}}
                                </p>
                                <p class="help-block"
                                   v-if="isNumberValue">
                                    {{$t('entity.validation.number')}}
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="allowNotifications" class="control-label">{{$t('organization.allowNotifications')}}</label>
                            <input type="checkbox" class="form-check-input " name="allowNotifications" id="allowNotifications"
                                   v-model="organization.allowNotifications" value="true">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-primary" :class="isAnyFieldError" @click="createOrganization" >
                            <span class="fas fa-download"></span>&nbsp;<span>{{$t('entity.action.save')}}</span>
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
                        <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <p>{{$t('organization.delete.question', {id: organization.id})}}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-danger" @click="confirmDelete(organization.id)">
                            <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>{{$t('organization.name')}}</th>
                    <th>{{$t('organization.description')}}</th>
                    <th>{{$t('organization.displayOrder')}}</th>
                    <th>{{$t('organization.allowNotifications')}}</th>
                    <th>{{$t('organization.identifiers')}}</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="organization in organizations" :key="organization.id">
                    <td><router-link :to="{ name: 'AdminEntityOrganizationDetails', params: { id: organization.id }}">
                    {{organization.id}}</router-link></td>
                    <td>{{organization.name}}</td>
                    <td>{{organization.description}}</td>
                    <td>{{organization.displayOrder}}</td>
                    <td><input type="checkbox" v-model="organization.allowNotifications" disabled/></td>
                    <td><span class="list-comma" v-for="identifierId in organization.identifiers" :key="identifierId">{{ identifierId }}</span></td>
                    <td>
                        <button type="submit"
                                @click="organizationDetail(organization.id)"
                                class="btn btn-info btn-sm me-1">
                            <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
                        </button>
                        <button type="submit"
                                @click="update(organization.id)"
                                class="btn btn-primary btn-sm me-1" v-has-any-role="'ROLE_ADMIN'">
                            <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
                        </button>
                        <button type="submit"
                                @click="deleteOrganization(organization.id)"
                                class="btn btn-danger btn-sm" v-has-any-role="'ROLE_ADMIN'">
                            <span class="far fa-times-circle"></span>&nbsp;<span>{{$t("entity.action.delete")}}</span>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
</template>

<script>
import OrganizationService from '@/services/entities/organization/OrganizationService'
import { Modal } from 'bootstrap'
export default {
  name: 'Organization',
  data () {
    return {
      organizations: [],
      organization: { name: null, displayName: null, description: null, displayOrder: 0, allowNotifications: false, identifiers: [], id: null },
      deleteModal: null,
      updateModal: null,
      errors: new Map()
    }
  },
  computed: {
    // Méthodes en charge du lancement des messages d'erreurs
    // sur les champs du formulaire de création et de mise à jour
    nameFieldRequired () {
      if (this.organization.name === null || this.organization.name.trim().length === 0 || this.organization.name === '') {
        return true
      } else {
        return false
      }
    },
    nameMinLength () {
      return this.errors.get('name') === 'minlength'
    },
    nameMaxLength () {
      return this.errors.get('name') === 'maxlength'
    },
    descriptionFieldRequired () {
      if (this.organization.description === null || this.organization.description.trim().length === 0 || this.organization.description === '') {
        return true
      } else {
        return false
      }
    },
    descriptionMinLength () {
      return this.errors.get('description') === 'minlength'
    },
    descriptionMaxLength () {
      return this.errors.get('description') === 'maxlength'
    },
    identifiersFieldRequired () {
      if (this.organization.identifiers === null || this.organization.identifiers.length === 0) {
        return true
      } else {
        return false
      }
    },
    displayOrderMinLength () {
      return this.errors.get('displayOrder') === 'minlength'
    },
    displayOrderMaxLength () {
      return this.errors.get('displayOrder') === 'maxlength'
    },
    isNumberValue () {
      return typeof this.organization.displayOrder === 'string'
    },

    // Méthode en charge d'activer ou non le boutton
    // de sauvegarde du formulaire de saisie
    isAnyFieldError () {
      if (this.nameFieldRequired || this.nameMinLength || this.nameMaxLength || this.nameFieldRequired || this.descriptionMinLength ||
      this.descriptionMaxLength || this.identifiersFieldRequired || this.displayOrderMinLength || this.displayOrderMaxLength) {
        return 'disabled'
      }
      return null
    }
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets structures
    loadAll () {
      OrganizationService.query().then(response => {
        this.organizations = response.data
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode permettant d'initialiser la map contenant
    // les types d'erreurs pour chaque champs de formulaire
    initMapError () {
      this.errors.set('name', null)
      this.errors.set('description', null)
      this.errors.set('displayOrder', null)
    },
    reset () {
      this.organizations = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de structure
    createOrganization () {
      this.organization.displayName = this.organization.name
      if (typeof this.organization.identifiers === 'string') {
        var identifiersValues = this.organization.identifiers
        this.organization.identifiers = []
        if (identifiersValues.includes(',')) {
          identifiersValues = identifiersValues.split(',')
          for (var i = 0; i < identifiersValues.length; i++) {
            this.organization.identifiers.push(identifiersValues[i])
          }
        } else {
          this.organization.identifiers.push(identifiersValues)
        }
      }
      OrganizationService.update(this.organization).then(() => {
        this.updateModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de structure
    update (id) {
      OrganizationService.get(id).then(response => {
        this.organization = response.data
        this.updateModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de structure
    deleteOrganization (id) {
      OrganizationService.get(id).then(response => {
        this.organization = response.data
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge de supprimer une structure
    confirmDelete (id) {
      OrganizationService.delete(id).then(() => {
        this.deleteModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    clear () {
      this.organization = { name: null, displayName: null, description: null, displayOrder: 0, allowNotifications: false, identifiers: [], id: null }
    },
    organizationDetail (organizationId) {
      this.$router.push({ name: 'AdminEntityOrganizationDetails', params: { id: organizationId } })
    },
    setError (fieldName, val, min, max) {
      if (val != null && val.length < min) {
        this.errors.set(fieldName, 'minlength')
      } else if (val != null && val.length > max) {
        this.errors.set(fieldName, 'maxlength')
      } else {
        this.errors.set(fieldName, null)
      }
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deleteOrganizationConfirmation)
    this.updateModal = new Modal(this.$refs.saveOrganizationModal)
    this.loadAll()
    this.initMapError()
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'organization.name' (newVal) {
      this.setError('name', newVal, 5, 255)
    },
    'organization.description' (newVal) {
      this.setError('description', newVal, 5, 512)
    },
    'organization.displayOrder' (newVal) {
      if (newVal != null && newVal < 0) {
        this.errors.set('displayOrder', 'minlength')
      } else if (newVal != null && newVal > 999) {
        this.errors.set('displayOrder', 'maxlength')
      } else {
        this.errors.set('displayOrder', null)
      }
    }
  }
}
</script>
