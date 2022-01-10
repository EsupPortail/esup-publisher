<template>
<div class='publisher'>
    <h2 >{{$t('publisher.home.title')}}</h2>
    <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#savePublisherModal" @click="clearCreate">
        <span class="fas fa-bolt"></span> <span >{{$t('publisher.home.createLabel')}}</span>
    </button>
    <div class="modal fade" id="savePublisherModal" tabindex="-1" role="dialog" aria-labelledby="myPublisherLabel"
         aria-hidden="true" ref="savePublisherModal">
        <div class="modal-dialog modal-fullscreen-md-down modal-lg">
            <div class="modal-content">
                <form name="editForm" role="form" novalidate show-validation>
                    <div class="modal-header">
                        <h4 class="modal-title" id="myPublisherLabel">{{$t('publisher.home.createOrEditLabel')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="control-label" for="ID" >ID</label>
                            <input type="text" class="form-control" name="id" id="ID"
                                   v-model="publisher.id" disabled>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="organization" >{{$t('publisher.context.organization')}}</label>
                            <select class="form-control" id="organization" name="organization" v-model="publisher.context.organization" required>
                                <option v-for="organization in organizations" v-bind:key="organization.id" :value="organization" >
                                    {{ organization.name }}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="reader" >{{$t('publisher.context.reader')}}</label>
                            <select class="form-control" id="reader" name="reader" v-model="publisher.context.reader" required>
                                <option v-for="reader in readers" v-bind:key="reader.id" :value="reader">
                                    {{ reader.name }}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="redactor" >{{$t('publisher.context.redactor')}}</label>
                            <select class="form-control" id="redactor" name="redactor" v-model="publisher.context.redactor" required>
                                <option v-for="redactor in redactors" v-bind:key="redactor.id" :value="redactor">
                                    {{ redactor.displayName }}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for ="displayName" class="control-label">{{$t('publisher.displayName')}}</label>
                            <input type="text" class="form-control" :class="(displayNameMinLength || displayNameMaxLength) ? 'is-invalid' : 'valid'" name="displayName" id="displayName"
                                   v-model="publisher.displayName">

                            <div>
                                <p class="help-block"
                                   v-if="displayNameFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="displayNameMinLength">
                                    {{$t('entity.validation.minlength', {min:'3'})}}
                                </p>
                                <p class="help-block"
                                   v-if="displayNameMaxLength" >
                                    {{$t('entity.validation.maxlength', {max:'50'})}}
                                </p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="permissionType" >{{$t('publisher.permissionType')}}</label>
                            <select class="form-control" id="permissionType" name="permissionType" v-model="publisher.permissionType" required>
                                <option v-for="permissionClass in permissionClasses" v-bind:key="permissionClass.id" :value="permissionClass.name">
                                    {{$t(permissionClass.label)}}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="control-label" for="defaultDisplayOrder" >{{$t('publisher.defaultDisplayOrder')}}</label>
                            <select class="form-control" id="defaultDisplayOrder" name="defaultDisplayOrder" v-model="publisher.defaultDisplayOrder" required>
                                <option v-for="defaultDisplayOrderType in displayOrderTypeList" v-bind:key="defaultDisplayOrderType.id" :value="defaultDisplayOrderType.name">
                                    {{$t(defaultDisplayOrderType.label)}}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="used" class="control-label">{{$t('publisher.used')}}</label>
                            <input type="checkbox" class="form-check-input " name="used" id="used"
                                   v-model="publisher.used" value="true">
                        </div>

                        <div class="form-group">
                            <label for="displayOrder" class="control-label">{{$t('publisher.displayOrder')}}</label>
                            <input type="number" class="form-control" :class="(displayOrderMinLength || displayOrderMaxLength) ? 'is-invalid' : 'valid'" name="displayOrder" id="displayOrder"
                                   v-model="publisher.displayOrder" required min="0" max="9">

                            <div>
                                <p class="help-block"
                                   v-if="displayOrderMinLength">
                                    {{$t('entity.validation.min', {min:'0'})}}
                                </p>
                                <p class="help-block"
                                   v-if="displayOrderMaxLength">
                                    {{$t('entity.validation.max', {max:'9'})}}
                                </p>
                                <p class="help-block"
                                   v-if="isNumberValue">
                                    {{$t('entity.validation.number')}}
                                </p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="hasSubPermsManagement" class="control-label">{{$t('publisher.hasSubPermsManagement')}}</label>
                            <input type="checkbox" class="form-check-input " name="hasSubPermsManagement" id="hasSubPermsManagement"
                                   v-model="publisher.hasSubPermsManagement" value="true">
                        </div>

                        <div class="form-group">
                            <label for="doHighlight" class="control-label">{{$t('publisher.doHighlight')}}</label>
                            <input type="checkbox" class="form-check-input " name="doHighlight" id="doHighlight"
                                   v-model="publisher.doHighlight" value="true">
                        </div>

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-primary" :class="isAnyFieldError" @click="createPublisher" >
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
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <p>{{$t('publisher.delete.question', {id: publisher.id})}}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-danger" @click="confirmDelete(publisher.id)">
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
                    <th>{{$t('publisher.context.key')}}</th>
                    <th>{{$t('publisher.displayName')}}</th>
                    <th>{{$t('publisher.permissionType')}}</th>
                    <th>{{$t('publisher.defaultDisplayOrder')}}</th>
                    <th>{{$t('publisher.used')}}</th>
                    <th>{{$t('publisher.displayOrder')}}</th>
                    <th>{{$t('publisher.hasSubPermsManagement')}}</th>
                    <th>{{$t('publisher.doHighlight')}}</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="publisher in publishers" :key="publisher.id">
                    <td><router-link :to="{ name: 'AdminEntityPublisherDetails', params: { id: publisher.id }}">
                    {{publisher.id}}</router-link></td>
                    <td>{{publisher.context.reader.displayName}} - {{publisher.context.redactor.displayName}} - {{publisher.context.organization.name}}</td>
                    <td>{{publisher.displayName}}</td>
                    <td>{{$t(getPermissionClassLabel(publisher.permissionType))}}</td>
                    <td>{{$t(getDisplayOrderTypeLabel(publisher.defaultDisplayOrder))}}</td>
                    <td><input type="checkbox" v-model="publisher.used" disabled/></td>
                    <td>{{publisher.displayOrder}}</td>
                    <td><input type="checkbox" v-model="publisher.hasSubPermsManagement" disabled/></td>
                    <td><input type="checkbox" v-model="publisher.doHighlight" disabled/></td>
                    <td>
                        <button type="submit"
                                @click="publisherDetail(publisher.id)"
                                class="btn btn-info btn-sm me-1">
                            <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
                        </button>
                        <button type="submit"
                                @click="update(publisher.id)"
                                class="btn btn-primary btn-sm me-1">
                            <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
                        </button>
                        <button type="submit"
                                @click="deletePublisher(publisher.id)"
                                class="btn btn-danger btn-sm">
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
import PublisherService from '@/services/entities/publisher/PublisherService'
import RedactorService from '@/services/entities/redactor/RedactorService'
import OrganizationService from '@/services/entities/organization/OrganizationService'
import ReaderService from '@/services/entities/reader/ReaderService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import { Modal } from 'bootstrap'

export default {
  name: 'Publisher',
  data () {
    return {
      publishers: [],
      redactors: [],
      organizations: [],
      readers: [],
      publisher: { context: { organization: {}, redactor: {}, reader: {} }, displayName: null, defaultDisplayOrder: null, permissionType: null, used: false, displayOrder: 0, hasSubPermsManagement: false, doHighlight: false, id: null },
      deleteModal: null,
      updateModal: null,
      errors: new Map()
    }
  },
  computed: {
    // Liste des types de permission
    permissionClasses () {
      return EnumDatasService.getPermissionClassList()
    },
    // Liste des types de Display Order
    displayOrderTypeList () {
      return EnumDatasService.getDisplayOrderTypeList()
    },
    // Pour les erreurs etc...
    organizationFieldRequired () {
      if (this.publisher.context.organization === null || this.publisher.context.organization.id === null || this.publisher.context.organization.id === '') {
        return true
      } else {
        return false
      }
    },
    readerFieldRequired () {
      if (this.publisher.context.reader === null || this.publisher.context.reader.id === null || this.publisher.context.reader.id === '') {
        return true
      } else {
        return false
      }
    },
    redactorFieldRequired () {
      if (this.publisher.context.redactor === null || this.publisher.context.redactor.id === null || this.publisher.context.redactor.id === '') {
        return true
      } else {
        return false
      }
    },
    displayNameFieldRequired () {
      if (this.publisher.displayName === null || this.publisher.displayName.trim().length === 0 || this.publisher.displayName === '') {
        return true
      } else {
        return false
      }
    },
    displayNameMinLength () {
      return this.errors.get('displayName') === 'minlength'
    },
    displayNameMaxLength () {
      return this.errors.get('displayName') === 'maxlength'
    },
    displayOrderMinLength () {
      return this.errors.get('displayOrder') === 'minlength'
    },
    displayOrderMaxLength () {
      return this.errors.get('displayOrder') === 'maxlength'
    },
    isNumberValue () {
      return typeof this.publisher.displayOrder === 'string'
    },
    // Méthode en charge d'activer ou non le boutton
    // de sauvegarde du formulaire de saisie
    isAnyFieldError () {
      if (this.organizationFieldRequired || this.readerFieldRequired || this.redactorFieldRequired ||
      this.displayOrderMinLength || this.displayOrderMaxLength ||
      this.displayNameFieldRequired || this.displayNameMinLength || this.displayNameMaxLength) {
        return 'disabled'
      }
      return null
    }
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets contexte de publication
    loadAll () {
      PublisherService.query().then(response => {
        this.publishers = response
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode permettant d'initialiser la map contenant
    // les types d'erreurs pour chaque champs de formulaire
    initMapError () {
      this.errors.set('organization', null)
      this.errors.set('reader', null)
      this.errors.set('redactor', null)
      this.errors.set('displayOrder', null)
      this.errors.set('displayName', null)
      this.errors.set('used', null)
    },
    reset () {
      this.publishers = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de contexte de publication
    createPublisher () {
      PublisherService.update(this.publisher).then(() => {
        this.updateModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de contexte de publication
    update (id) {
      PublisherService.get(id).then(result => {
        this.publisher = result
        this.updateModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de contexte de publication
    deletePublisher (id) {
      PublisherService.get(id).then(result => {
        this.publisher = result
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge de supprimer un contexte de publication
    confirmDelete (id) {
      PublisherService.delete(id).then(() => {
        this.deleteModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    clear () {
      this.publisher = { context: { organization: {}, redactor: {}, reader: {} }, displayName: null, defaultDisplayOrder: null, permissionType: null, used: false, displayOrder: 0, hasSubPermsManagement: false, doHighlight: false, id: null }
    },
    // Méthode pour clear et initialiser les valeurs pour les valeurs présélectionées dans la modale de création
    clearCreate () {
      this.clear()
      this.publisher.context.organization = this.organizations && this.organizations.length > 0 ? this.organizations[0] : {}
      this.publisher.context.reader = this.readers && this.readers.length > 0 ? this.readers[0] : {}
      this.publisher.context.redactor = this.redactors && this.redactors.length > 0 ? this.redactors[0] : {}
      this.publisher.permissionType = this.permissionClasses && this.permissionClasses.length > 0 ? this.permissionClasses[0].name : null
      this.publisher.defaultDisplayOrder = this.displayOrderTypeList && this.displayOrderTypeList.length > 0 ? this.displayOrderTypeList[0].name : null
    },
    publisherDetail (publisherId) {
      this.$router.push({ name: 'AdminEntityPublisherDetails', params: { id: publisherId } })
    },
    setError (fieldName, val, min, max) {
      if (val != null && val.length < min) {
        this.errors.set(fieldName, 'minlength')
      } else if (val != null && val.length > max) {
        this.errors.set(fieldName, 'maxlength')
      } else {
        this.errors.set(fieldName, null)
      }
    },
    getPermissionClassLabel (name) {
      return this.getEnumlabel('permissionClass', name)
    },
    getDisplayOrderTypeLabel (name) {
      return this.getEnumlabel('displayOrderType', name)
    },
    getEnumlabel (type, name) {
      var data
      switch (type) {
        case 'permissionClass':
          data = this.permissionClasses.find(val => {
            return val.name === name
          })
          return data ? data.label : ''
        case 'displayOrderType':
          data = this.displayOrderTypeList.find(val => {
            return val.name === name
          })
          return data ? data.label : ''
      }
      return ''
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deletePublisherConfirmation)
    this.updateModal = new Modal(this.$refs.savePublisherModal)
    this.loadAll()
    this.initMapError()
  },
  created () {
    RedactorService.query().then(response => {
      this.redactors = response
      this.publisher.context.redactor = this.redactors[0]
    }).catch(error => {
      console.error(error)
    })
    OrganizationService.query().then(response => {
      this.organizations = response
      this.publisher.context.organization = this.organizations[0]
    }).catch(error => {
      console.error(error)
    })
    ReaderService.query().then(response => {
      this.readers = response
      this.publisher.context.reader = this.readers[0]
    }).catch(error => {
      console.error(error)
    })
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'publisher.displayName' (newVal) {
      this.setError('displayName', newVal, 3, 50)
    },
    'publisher.displayOrder' (newVal) {
      if (newVal != null && newVal < 0) {
        this.errors.set('displayOrder', 'minlength')
      } else if (newVal != null && newVal > 9) {
        this.errors.set('displayOrder', 'maxlength')
      } else {
        this.errors.set('displayOrder', null)
      }
    }
  }
}
</script>
