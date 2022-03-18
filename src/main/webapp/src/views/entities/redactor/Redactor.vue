<template>
<div class='redactor'>
    <h2 >{{$t('redactor.home.title')}}</h2>
    <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#saveRedactorModal" @click="clear();initFormValidator()">
        <span class="fas fa-bolt"></span> <span >{{$t('redactor.home.createLabel')}}</span>
    </button>
    <div class="modal fade" id="saveRedactorModal" tabindex="-1" role="dialog" aria-labelledby="myRedactorLabel"
         aria-hidden="true" ref="saveRedactorModal">
        <div class="modal-dialog modal-fullscreen-md-down modal-lg">
            <div class="modal-content">
                <form name="editForm" role="form" class="was-validated" novalidate show-validation>
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
                            <input type="text" class="form-control" name="name" id="name"
                                   v-model="redactor.name" required minlength="3" maxlength="20">
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('name', formErrors.REQUIRED)">
                                {{$t('entity.validation.required')}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)">
                                {{$t('entity.validation.minlength', {min:'3'})}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)">
                                {{$t('entity.validation.maxlength', {max:'20'})}}
                            </div>
                        </div>
                         <div class="form-group">
                            <label for="displayName" class="control-label">{{$t('redactor.displayName')}}</label>
                            <input type="text" class="form-control" name="displayName" id="displayName"
                                   v-model="redactor.displayName" required minlength="3" maxlength="50">
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
                         <div class="form-group">
                            <label for="description" class="control-label">{{$t('redactor.description')}}</label>
                            <input type="text" class="form-control" name="description" id="description"
                                   v-model="redactor.description" required minlength="5" maxlength="512">
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('description', formErrors.REQUIRED)">
                                {{$t('entity.validation.required')}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('description', formErrors.MIN_LENGTH)">
                                {{$t('entity.validation.minlength', {min:'5'})}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('description', formErrors.MAX_LENGTH)">
                                {{$t('entity.validation.maxlength', {max:'512'})}}
                            </div>
                        </div>
                        <div class="form-group">
                          <label for ="format" class="control-label">{{$t('redactor.format')}}</label>
                          <select class="form-select"  id="format" v-model="redactor.format" required>
                            <option v-for="format in writingFormatList" :key="format.id" :value="format">{{format}}</option>
                          </select>
                          <div class="invalid-feedback"
                              v-if="formValidator.hasError('format', formErrors.REQUIRED)">
                              {{$t('entity.validation.required')}}
                          </div>
                        </div>
                        <div class="form-group">
                          <label for ="writingMode" class="control-label">{{$t('redactor.writingMode')}}</label>
                          <select class="form-select"  id="writingMode" v-model="redactor.writingMode" required>
                            <option v-for="writingMode in this.writingModeList" :key="writingMode.id" :value="writingMode.name">{{$t(this.getWritingModeLabel(writingMode.name))}}</option>
                          </select>
                          <div class="invalid-feedback"
                            v-if="formValidator.hasError('writingMode', formErrors.REQUIRED)">
                            {{$t('entity.validation.required')}}
                          </div>
                        </div>
                        <div class="form-group">
                            <label for="nbLevelsOfClassification" class="control-label">{{$t('redactor.nbLevelsOfClassification')}}</label>
                            <input type="number" class="form-control" name="nbLevelsOfClassification" id="nbLevelsOfClassification"
                                   v-model="redactor.nbLevelsOfClassification" required min="1" max="2">
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('nbLevelsOfClassification', formErrors.REQUIRED)">
                                {{$t('entity.validation.required')}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('nbLevelsOfClassification', formErrors.MIN_VALUE)">
                                {{$t('entity.validation.min', {min:'1'})}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('nbLevelsOfClassification', formErrors.MAX_VALUE)">
                                {{$t('entity.validation.max', {max:'2'})}}
                            </div>
                        </div>
                        <div class="form-group">
                          <label for ="optionalPublishTime" class="control-label">{{$t('redactor.optionalPublishTime')}}</label>
                          <input type="checkbox" class="form-check-input d-block mx-auto" name="optionalPublishTime" id="optionalPublishTime"
                                   v-model="redactor.optionalPublishTime" value="false">
                        </div>
                        <div class="form-group">
                            <label for="nbDaysMaxDuration" class="control-label">{{$t('redactor.nbDaysMaxDuration')}}</label>
                            <input type="number" class="form-control" name="nbDaysMaxDuration" id="nbDaysMaxDuration"
                                   v-model="redactor.nbDaysMaxDuration" required min="1" max="999">
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('nbDaysMaxDuration', formErrors.REQUIRED)">
                                {{$t('entity.validation.required')}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('nbDaysMaxDuration', formErrors.MIN_VALUE)">
                                {{$t('entity.validation.min', {min:'1'})}}
                            </div>
                            <div class="invalid-feedback"
                                v-if="formValidator.hasError('nbDaysMaxDuration', formErrors.MAX_VALUE)">
                                {{$t('entity.validation.max', {max:'999'})}}
                            </div>
                        </div>
                        <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-primary" :disabled="formValidator.hasError()" @click="createRedactor" >
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
import { FormValidationUtils, FormErrorType } from '@/services/util/FormValidationUtils'
import { Modal } from 'bootstrap'

export default {
  name: 'Redactor',
  data () {
    return {
      redactors: [],
      redactor: { name: null, displayName: null, description: null, format: null, writingMode: null, nbLevelsOfClassification: 1, optionalPublishTime: false, nbDaysMaxDuration: 168, id: null },
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType
    }
  },
  computed: {
    writingFormatList () {
      return EnumDatasService.getWritingFormatList()
    },
    writingModeList () {
      return EnumDatasService.getWritingModeList()
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
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator () {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('name', this.redactor.name, 3, 20, true)
      this.formValidator.checkTextFieldValidity('displayName', this.redactor.displayName, 3, 50, true)
      this.formValidator.checkTextFieldValidity('description', this.redactor.description, 5, 512, true)
      this.formValidator.checkTextFieldValidity('format', this.redactor.format, null, null, true)
      this.formValidator.checkTextFieldValidity('writingMode', this.redactor.writingMode, null, null, true)
      this.formValidator.checkNumberFieldValidity('nbLevelsOfClassification', this.redactor.nbLevelsOfClassification, 1, 2, true)
      this.formValidator.checkNumberFieldValidity('nbDaysMaxDuration', this.redactor.nbDaysMaxDuration, 1, 999, true)
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
        this.initFormValidator()
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
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'redactor.name' (newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 3, 20, true)
    },
    'redactor.displayName' (newVal) {
      this.formValidator.checkTextFieldValidity('displayName', newVal, 3, 50, true)
    },
    'redactor.description' (newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 5, 512, true)
    },
    'redactor.format' (newVal) {
      this.formValidator.checkTextFieldValidity('format', newVal, null, null, true)
    },
    'redactor.writingMode' (newVal) {
      this.formValidator.checkTextFieldValidity('writingMode', newVal, null, null, true)
    },
    'redactor.nbLevelsOfClassification' (newVal) {
      this.formValidator.checkNumberFieldValidity('nbLevelsOfClassification', newVal, 1, 2, true)
    },
    'redactor.nbDaysMaxDuration' (newVal) {
      this.formValidator.checkNumberFieldValidity('nbDaysMaxDuration', newVal, 1, 999, true)
    }
  }
}
</script>
