<template>
<div class='redactor'>
    <h2 >{{$t('redactor.home.title')}}</h2>
    <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#saveRedactorModal" @click="clear">
        <span class="fas fa-bolt"></span> <span >{{$t('redactor.home.createLabel')}}</span>
    </button>
    <div class="modal fade" id="saveRedactorModal" tabindex="-1" role="dialog" aria-labelledby="myRedactorLabel"
         aria-hidden="true" ref="saveRedactorModal">
        <div class="modal-dialog modal-fullscreen-md-down modal-lg">
            <div class="modal-content">
                <form name="editForm" role="form" novalidate show-validation>
                    <div class="modal-header">
                        <h4 class="modal-title" id="myRedactorLabel">{{$t('redactor.home.createOrEditLabel')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="control-label" for="ID" >ID</label>
                            <input type="text" class="form-control" name="id" id="ID"
                                   v-model="redactor.id" disabled>
                        </div>

                        <div class="form-group">
                            <label for ="name" class="control-label">{{$t('redactor.name')}}</label>
                            <input type="text" class="form-control" :class="(nameMinLength || nameMaxLength) ? 'is-invalid' : 'valid'" name="name" id="name"
                                   v-model="redactor.name">

                            <div>
                                <p class="help-block"
                                   v-if="nameFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="nameMinLength">
                                    {{$t('entity.validation.minlength', {min:'3'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nameMaxLength" >
                                    {{$t('entity.validation.maxlength', {max:'20'})}}
                                </p>
                            </div>
                        </div>
                         <div class="form-group">
                            <label for="displayName" class="control-label">{{$t('redactor.displayName')}}</label>
                            <input type="text" class="form-control" :class="(displayNameMinLength || displayNameMaxLength) ? 'is-invalid' : 'valid'" name="displayName" id="displayName"
                                   v-model="redactor.displayName" required>

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
                                   v-if="displayNameMaxLength">
                                    {{$t('entity.validation.maxlength', {max:'50'})}}
                                </p>
                            </div>
                        </div>
                         <div class="form-group">
                            <label for="description" class="control-label">{{$t('redactor.description')}}</label>
                            <input type="text" class="form-control" :class="(descriptionMinLength || descriptionMaxLength) ? 'is-invalid' : 'valid'" name="description" id="description"
                                   v-model="redactor.description" required>

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
                          <label for ="format" class="control-label">{{$t('redactor.format')}}</label>
                          <select class="form-select"  id="format" v-model="redactor.format" required>
                            <option v-for="format in writingFormatList" :key="format.id" :value="format">{{format}}</option>
                          </select>
                          <div>
                              <p class="help-block"
                                  v-if="formatFieldRequired">
                                  {{$t('entity.validation.required')}}
                              </p>
                          </div>
                        </div>
                        <div class="form-group">
                          <label for ="writingMode" class="control-label">{{$t('redactor.writingMode')}}</label>
                          <select class="form-select"  id="writingMode" v-model="redactor.writingMode" required>
                            <option v-for="writingMode in this.writingModeList" :key="writingMode.id" :value="writingMode.name">{{$t(this.getWritingModeLabel(writingMode.name))}}</option>
                          </select>
                           <div>
                              <p class="help-block"
                                  v-if="writingModeFieldRequired">
                                  {{$t('entity.validation.required')}}
                              </p>
                          </div>
                        </div>
                        <div class="form-group">
                            <label for="nbLevelsOfClassification" class="control-label">{{$t('redactor.nbLevelsOfClassification')}}</label>
                            <input type="number" class="form-control" :class="(nbLevelsOfClassificationMinLength || nbLevelsOfClassificationMaxLength) ? 'is-invalid' : 'valid'" name="nbLevelsOfClassification" id="nbLevelsOfClassification"
                                   v-model="redactor.nbLevelsOfClassification" required min="1" max="2">

                            <div>
                                <p class="help-block"
                                   v-if="nbLevelsOfClassificationMinLength">
                                    {{$t('entity.validation.minlength', {min:'1'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nbLevelsOfClassificationMaxLength">
                                    {{$t('entity.validation.maxlength', {max:'2'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nbLevelsOfClassificationIsNumberValue">
                                    {{$t('entity.validation.number')}}
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                          <label for ="optionalPublishTime" class="control-label">{{$t('redactor.optionalPublishTime')}}</label>
                          <input type="checkbox" class="form-check-input " name="optionalPublishTime" id="optionalPublishTime"
                                   v-model="redactor.optionalPublishTime" value="false">
                        </div>
                        <div class="form-group">
                            <label for="nbDaysMaxDuration" class="control-label">{{$t('redactor.nbDaysMaxDuration')}}</label>
                            <input type="number" class="form-control" :class="(nbDaysMaxDurationMinLength || nbDaysMaxDurationMaxLength) ? 'is-invalid' : 'valid'" name="nbDaysMaxDuration" id="nbDaysMaxDuration"
                                   v-model="redactor.nbDaysMaxDuration" required min="1" max="999">

                            <div>
                                <p class="help-block"
                                   v-if="nbDaysMaxDurationMinLength">
                                    {{$t('entity.validation.minlength', {min:'1'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nbDaysMaxDurationMaxLength">
                                    {{$t('entity.validation.maxlength', {max:'999'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nbDaysMaxDurationIsNumberValue">
                                    {{$t('entity.validation.number')}}
                                </p>
                            </div>
                        </div>
                        <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-primary" :class="isAnyFieldError" @click="createRedactor" >
                            <span class="fas fa-download"></span>&nbsp;<span>{{$t('entity.action.save')}}</span>
                        </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="modal fade" ref="deleteRedactorConfirmation">
        <div class="modal-dialog">
            <div class="modal-content">
                <form name="deleteForm">
                    <div class="modal-header">
                        <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <p>{{$t('redactor.delete.question', {id: redactor.id})}}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-danger" @click="confirmDelete(redactor.id)">
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
                    <th>{{$t('redactor.name')}}</th>
                    <th>{{$t('redactor.displayName')}}</th>
                    <th>{{$t('redactor.description')}}</th>
                    <th>{{$t('redactor.format')}}</th>
                    <th>{{$t('redactor.writingMode')}}</th>
                    <th>{{$t('redactor.nbLevelsOfClassification')}}</th>
                    <th>{{$t('redactor.optionalPublishTime')}}</th>
                    <th>{{$t('redactor.nbDaysMaxDuration')}}</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="redactor in redactors" :key="redactor.id">
                    <td><router-link :to="{ name: 'AdminEntityRedactorDetails', params: { id: redactor.id }}">
                    {{redactor.id}}</router-link></td>
                    <td>{{redactor.name}}</td>
                    <td>{{redactor.displayName}}</td>
                    <td>{{redactor.description}}</td>
                    <td>{{redactor.format}}</td>
                    <td>{{$t(this.getWritingModeLabel(redactor.writingMode))}}</td>
                    <td>{{redactor.nbLevelsOfClassification}}</td>
                    <td><input type="checkbox" v-model="redactor.optionalPublishTime" disabled/></td>
                    <td>{{redactor.nbDaysMaxDuration}}</td>
                    <td>
                        <button type="submit"
                                @click="redactorDetail(redactor.id)"
                                class="btn btn-info btn-sm me-1">
                            <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
                        </button>
                        <button type="submit"
                                @click="update(redactor.id)"
                                class="btn btn-primary btn-sm me-1">
                            <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
                        </button>
                        <button type="submit"
                                @click="deleteRedactor(redactor.id)"
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
import RedactorService from '@/services/entities/redactor/RedactorService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import { Modal } from 'bootstrap'

export default {
  name: 'Redactor',
  data () {
    return {
      redactors: [],
      redactor: { name: null, displayName: null, description: null, format: null, writingMode: null, nbLevelsOfClassification: 1, optionalPublishTime: false, nbDaysMaxDuration: 168, id: null },
      deleteModal: null,
      updateModal: null,
      errors: new Map()
    }
  },
  computed: {
    writingFormatList () {
      return EnumDatasService.getWritingFormatList()
    },
    writingModeList () {
      return EnumDatasService.getWritingModeList()
    },
    // Méthodes en charge du lancement des messages d'erreurs
    // sur les champs du formulaire de création et de mise à jour
    nameFieldRequired () {
      if (this.redactor.name === null || this.redactor.name.trim().length === 0 || this.redactor.name === '') {
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
    displayNameFieldRequired () {
      if (this.redactor.displayName === null || this.redactor.displayName.trim().length === 0 || this.redactor.displayName === '') {
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
    descriptionFieldRequired () {
      if (this.redactor.description === null || this.redactor.description.trim().length === 0 || this.redactor.description === '') {
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
    formatFieldRequired () {
      if (this.redactor.format === null || this.redactor.format.trim().length === 0 || this.redactor.format === '') {
        return true
      } else {
        return false
      }
    },
    writingModeFieldRequired () {
      if (this.redactor.writingMode === null || this.redactor.writingMode.trim().length === 0 || this.redactor.writingMode === '') {
        return true
      } else {
        return false
      }
    },
    nbLevelsOfClassificationFieldRequired () {
      if (this.redactor.nbLevelsOfClassification === null || this.redactor.nbLevelsOfClassification === '') {
        return true
      } else {
        return false
      }
    },
    nbLevelsOfClassificationMinLength () {
      return this.errors.get('nbLevelsOfClassification') === 'minlength'
    },
    nbLevelsOfClassificationMaxLength () {
      return this.errors.get('nbLevelsOfClassification') === 'maxlength'
    },
    nbDaysMaxDurationFieldRequired () {
      if (this.redactor.nbDaysMaxDuration === null || this.redactor.nbDaysMaxDuration === '') {
        return true
      } else {
        return false
      }
    },
    nbDaysMaxDurationMinLength () {
      return this.errors.get('nbDaysMaxDuration') === 'minlength'
    },
    nbDaysMaxDurationMaxLength () {
      return this.errors.get('nbDaysMaxDuration') === 'maxlength'
    },
    nbLevelsOfClassificationIsNumberValue () {
      return typeof this.redactor.nbLevelsOfClassification === 'string'
    },
    nbDaysMaxDurationIsNumberValue () {
      return typeof this.redactor.nbDaysMaxDuration === 'string'
    },
    // Méthode en charge d'activer ou non le boutton
    // de sauvegarde du formulaire de saisie
    isAnyFieldError () {
      if (this.nameFieldRequired || this.nameMinLength || this.nameMaxLength || this.nameFieldRequired || this.descriptionMinLength ||
      this.descriptionMaxLength || this.displayNameFieldRequired || this.displayNameMinLength || this.displayNameMaxLength || this.formatFieldRequired ||
      this.writingModeFieldRequired || this.nbLevelsOfClassificationFieldRequired || this.nbLevelsOfClassificationMinLength || this.nbLevelsOfClassificationMaxLength ||
      this.nbDaysMaxDurationFieldRequired || this.nbDaysMaxDurationMinLength || this.nbDaysMaxDurationMaxLength) {
        return 'disabled'
      }
      return null
    }
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets redacteurs
    loadAll () {
      RedactorService.query().then(response => {
        this.redactors = response.data
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode permettant d'initialiser la map contenant
    // les types d'erreurs pour chaque champs de formulaire
    initMapError () {
      this.errors.set('name', null)
      this.errors.set('displayName', null)
      this.errors.set('description', null)
      this.errors.set('format', null)
      this.errors.set('writingMode', null)
      this.errors.set('nbLevelsOfClassification', null)
      this.errors.set('nbDaysMaxDuration', null)
    },
    reset () {
      this.redactors = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de redacteur
    createRedactor () {
      RedactorService.update(this.redactor).then(() => {
        this.updateModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de redacteur
    update (id) {
      RedactorService.get(id).then(response => {
        this.redactor = response.data
        this.updateModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de redacteur
    deleteRedactor (id) {
      RedactorService.get(id).then(response => {
        this.redactor = response.data
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge de supprimer une redacteur
    confirmDelete (id) {
      RedactorService.delete(id).then(() => {
        this.deleteModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    clear () {
      this.redactor = { name: null, displayName: null, description: null, format: this.writingFormatList[0], writingMode: this.writingModeList[0].name, nbLevelsOfClassification: 1, optionalPublishTime: false, nbDaysMaxDuration: 168, id: null }
    },
    redactorDetail (redactorId) {
      this.$router.push({ name: 'AdminEntityRedactorDetails', params: { id: redactorId } })
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
    getWritingModeLabel (name) {
      if (name) {
        return this.writingModeList.filter(function (val) { return val.name === name })[0].label
      } else {
        return ''
      }
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deleteRedactorConfirmation)
    this.updateModal = new Modal(this.$refs.saveRedactorModal)
    this.loadAll()
    this.initMapError()
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'redactor.name' (newVal) {
      this.setError('name', newVal, 3, 20)
    },
    'redactor.displayName' (newVal) {
      this.setError('displayName', newVal, 3, 50)
    },
    'redactor.description' (newVal) {
      this.setError('description', newVal, 5, 512)
    },
    'redactor.nbLevelsOfClassification' (newVal) {
      if (newVal != null && newVal < 1) {
        this.errors.set('nbLevelsOfClassification', 'minlength')
      } else if (newVal != null && newVal > 2) {
        this.errors.set('nbLevelsOfClassification', 'maxlength')
      } else {
        this.errors.set('nbLevelsOfClassification', null)
      }
    },
    'redactor.nbDaysMaxDuration' (newVal) {
      if (newVal != null && newVal < 1) {
        this.errors.set('nbDaysMaxDuration', 'minlength')
      } else if (newVal != null && newVal > 999) {
        this.errors.set('nbDaysMaxDuration', 'maxlength')
      } else {
        this.errors.set('nbDaysMaxDuration', null)
      }
    }
  }
}
</script>
