<template>
  <div>
    <div class="resourceForm">
        <div class="form-group d-none">
            <label class="control-label" for="ID">ID</label>
            <input type="text" class="form-control" name="id" id="ID"
                   v-model="item.id" readonly>
        </div>

        <div class="form-group">
            <label class="control-label" for="title">{{ $t('resource.title') }}</label>
            <input type="text" class="form-control" name="title" id="title"
                   v-model="itemTitle" required minlength="3" maxlength="200">
            <div class="invalid-feedback"
                v-if="formValidator.hasError('title', formErrors.REQUIRED)">
                {{ $t('entity.validation.required') }}
            </div>
            <div class="invalid-feedback"
                v-if="formValidator.hasError('title', formErrors.MIN_LENGTH)">
                {{ $t('entity.validation.minlength', {min: '3'}) }}
            </div>
            <div class="invalid-feedback"
                v-if="formValidator.hasError('title', formErrors.MAX_LENGTH)">
                {{ $t('entity.validation.maxlength', {max: '200'}) }}
            </div>
        </div>
        <div class="form-group">
            <label class="control-label" for="enclosure">{{ $t('resource.enclosure') }}</label>
            <div class="form-inline">
                <button type="button" v-if="!item.enclosure" class="btn btn-light btn-file" data-bs-toggle="modal" data-bs-target="#cropImageModale" @click="clearUpload()">
                    <span>{{ $t('resource.enclosure-button') }}</span>
                </button>

                <img v-if="item.enclosure" id="enclosure" :src="getUrlFromEnclosure()"  class="img-responsive img-fluid" :alt="$t('resource.enclosure')" />
                <a v-if="item.enclosure" href="" data-bs-toggle="modal" data-bs-target="#deleteEnclosureConfirmation" v-tooltip="$t('entity.action.delete')">
                   <i class="far fa-times-circle text-danger"></i>
                </a>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label" for="summary">{{ $t('resource.summary') }}</label>
            <textarea class="form-control" name="summary" id="summary" rows="3" v-model="itemSummary" required minlength="5" maxlength="512"/>
            <div class="invalid-feedback"
                v-if="formValidator.hasError('summary', formErrors.REQUIRED)">
                {{ $t('entity.validation.required') }}
            </div>
            <div class="invalid-feedback"
                v-if="formValidator.hasError('summary', formErrors.MIN_LENGTH)">
                {{ $t('entity.validation.minlength', {min: '5'}) }}
            </div>
            <div class="invalid-feedback"
                v-if="formValidator.hasError('summary', formErrors.MAX_LENGTH)">
                {{ $t('entity.validation.maxlength', {min: '512'}) }}
            </div>
        </div>
        <div class="row">
            <div class="form-group col-md-4">
                <label class="control-label" for="startDate">{{ $t('resource.startDate') }}</label>
                <input type="date" class="form-control" name="startDate" id="startDate" placeholder="jj/mm/aaaa" v-model="itemStartDate"
                    required :min="formatDateToString(minDate)" :max="formatDateToString(startMaxDate)">
                <div class="invalid-feedback"
                    v-if="formValidator.hasError('startDate', formErrors.REQUIRED)">
                    {{ $t('entity.validation.required') }}
                </div>
                <div class="invalid-feedback"
                    v-if="formValidator.hasError('startDate', formErrors.MIN_DATE)">
                    {{ $t('entity.validation.mindate', { min: formatDateToIntString(minDate)}) }}
                </div>
                <div class="invalid-feedback"
                    v-if="formValidator.hasError('startDate', formErrors.MAX_DATE)">
                    {{ $t('entity.validation.maxdate', { max: formatDateToIntString(startMaxDate)}) }}
                </div>
            </div>
            <div class="form-group col-md-4">
                <label class="control-label" for="endDate">{{ $t('resource.endDate') }}</label>
                <span v-if="publisher.context.redactor.optionalPublishTime" class="d-block d-md-none">{{ $t('resource.detail.unlimitedEndDate') }}</span>
                <input type="date" class="form-control" name="endDate" id="endDate" placeholder="jj/mm/aaaa" v-model="itemEndDate"
                  :min="formatDateToString(endMinDate)" :max="formatDateToString(maxDate)"
                  v-tooltip="publisher.context.redactor.optionalPublishTime ? $t('resource.detail.unlimitedEndDate') : ''"
                  :required="!publisher.context.redactor.optionalPublishTime">
                <div class="invalid-feedback"
                    v-if="formValidator.hasError('endDate', formErrors.REQUIRED)">
                    {{ $t('entity.validation.required') }}
                </div>
                <div class="invalid-feedback"
                    v-if="formValidator.hasError('endDate', formErrors.MIN_DATE)">
                    {{ $t('entity.validation.mindate', { min: formatDateToIntString(endMinDate)}) }}
                </div>
                <div class="invalid-feedback"
                    v-if="formValidator.hasError('endDate', formErrors.MAX_DATE)">
                    {{ $t('entity.validation.maxdate', { max: formatDateToIntString(maxDate)}) }}
                </div>
            </div>
            <div class="form-group col-md-4 text-md-center">
                <label class="control-label" for="rssAllowed">{{ $t('resource.rssAllowed') }}</label>
                <input type="checkbox" class="form-check-input d-block mx-auto" name="rssAllowed" id="rssAllowed" v-model="itemRssAllowed" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label" for="ressourceUrl">{{ $t('resource.ressourceUrl') }}</label>
            <input type="url" class="form-control" name="ressourceUrl" id="ressourceUrl" placeholder="https://..."
                   v-model="itemRessourceUrl" required maxlength="2048" ref="ressourceUrlInput">
            <div class="invalid-feedback"
                v-if="formValidator.hasError('ressourceUrl', formErrors.REQUIRED)">
                {{ $t('entity.validation.required') }}
            </div>
            <div class="invalid-feedback"
                v-if="formValidator.hasError('ressourceUrl', formErrors.MAX_LENGTH)">
                {{ $t('entity.validation.maxlength', {max: '2048'}) }}
            </div>
            <div class="invalid-feedback"
                v-if="invalidRessourceUrl">
                {{ $t('entity.validation.url') }}
            </div>
        </div>
    </div>

    <div class="modal fade" id="cropImageModale" tabindex="-1" role="dialog" aria-labelledby="myCropImageModale"
         aria-hidden="true" ref="cropImageModale">
        <div class="modal-dialog modal-fullscreen-md-down">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">{{ $t('resource.inputfile.title') }}</h3>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"></button>
                </div>
                <div class="modal-body">
                    <form name="publishContentEnclosureForm" role="form" novalidate>
                        <div class="card">
                            <h3 class="card-header">{{ $t('resource.inputfile.url-label') }}</h3>
                            <div class="card-body">
                                <div class="form-group row">
                                  <div class="col-md-auto">
                                    <input type="url" class="form-control" name="enclosure" placeholder="https://..."
                                           @input="onSelectPicUrl()" maxlength="2048" ref="urlInput">
                                    <div class="invalid-feedback"
                                        v-if="urlInputError === formErrors.MAX_LENGTH">
                                        {{ $t('entity.validation.maxlength', {min: '2048'}) }}
                                    </div>
                                    <div class="invalid-feedback"
                                        v-if="urlInputError === 'invalidurl'">
                                        {{ $t('entity.validation.url') }}
                                    </div>
                                  </div>
                                  <div class="col-md-auto">
                                    <button type="button" :disabled="urlInputError !== null || !content.picUrl || content.picUrl.length === 0" class="btn btn-primary"
                                            @click="validatePicUrl(content.picUrl)">
                                        <span class="fas fa-download"></span>&nbsp;<span>{{ $t('entity.action.validate') }}</span>
                                    </button>
                                  </div>
                                </div>
                            </div>
                        </div>
                        <div class="card">
                            <div class="card-header">
                                <h3 class="d-inline-block me-2">{{ $t('resource.inputfile.upload-label') }}</h3>
                                <button type="button" class="btn btn-primary btn-file" name="fileEnclosure" @click="fileInput.click()">
                                  <i class="fas fa-folder-open"></i>&nbsp;<span>{{ $t('resource.inputfile.upload-button') }}</span>
                                </button>
                                <input type="file" id="file" name="file" ref="fileInput" accept="image/*" @change="onSelectFile()" hidden/>
                            </div>
                            <div class="card-body">
                              <div v-if="content.file" class="card">
                                  <h4 class="card-header mt-0">{{ $t('resource.inputfile.resizeauto') }}</h4>

                                  <div class="card-body">
                                    <div v-if="content.file && fileInputError === null">
                                        <img :src="getUrlFromBlob(content.file)" class="mw-100" alt="" />
                                        <button type="button" @click="uploadFile()" class="btn btn-primary">
                                            <span class="fas fa-download"></span>&nbsp;<span>{{ $t('entity.action.validate') }}</span></button>
                                    </div>
                                  </div>
                              </div>
                              <div class="invalid-feedback d-block"
                                  v-if="fileInputError === formErrors.PATTERN">
                                  {{ $t('entity.validation.filepattern') }}
                              </div>
                              <div class="invalid-feedback d-block"
                                  v-if="fileInputError === formErrors.MAX_SIZE">
                                  {{ $t('entity.validation.maxsize', {maxsize: '10MB'}) }}
                              </div>
                              <div class="progress" v-if="progress > 0">
                                  <div :class="'progress-bar progress-bar-striped bg-' + progressStatus" role="progressbar"
                                      :aria-valuenow="progress" aria-valuemin="0"
                                      aria-valuemax="100" :style="{'width': progress+'%'}">
                                      <span class="progress-text">{{progress}}%</span>
                                  </div>
                              </div>
                            </div>
                        </div>
                    </form>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default btn-outline-dark" title="Clear selected files" @click="clearUpload()">
                        <span class="fas fa-trash-alt"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
                    </button>
                    <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                        <span class="far fa-times-circle"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
                    </button>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script>
import ItemService from '@/services/entities/item/ItemService'
import { FormValidationUtils, FormErrorType } from '@/services/util/FormValidationUtils'
import DateUtils from '@/services/util/DateUtils'
import UploadUtils from '@/services/util/UploadUtils'
import store from '@/store/index.js'
import { Modal } from 'bootstrap'

export default {
  name: 'ContentResource',
  data () {
    return {
      urlInput: null,
      urlInputError: null,
      fileInput: null,
      fileInputError: null,
      cropImageModale: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
      ressourceUrlInput: null,
      invalidRessourceUrl: false
    }
  },
  inject: [
    'publisher', 'item', 'setItem', 'progress', 'setProgress', 'progressStatus', 'setProgressStatus', 'content', 'setContent',
    'minDate', 'endMinDate', 'maxDate', 'clearUpload', 'setItemValidated', 'uploadLinkedFile', 'startMaxDate'
  ],
  computed: {
    itemTitle: {
      get () {
        return this.item.title || ''
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.title = newVal
        this.setItem(newItem)
      }
    },
    itemSummary: {
      get () {
        return this.item.summary
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.summary = newVal
        this.setItem(newItem)
      }
    },
    itemStartDate: {
      get () {
        return this.formatDateToString(this.item.startDate)
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.startDate = this.formatStringToDate(newVal)
        this.setItem(newItem)
      }
    },
    itemEndDate: {
      get () {
        return this.formatDateToString(this.item.endDate)
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.endDate = this.formatStringToDate(newVal)
        this.setItem(newItem)
      }
    },
    itemRssAllowed: {
      get () {
        return this.item.rssAllowed
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.rssAllowed = newVal
        this.setItem(newItem)
      }
    },
    itemRessourceUrl: {
      get () {
        return this.item.ressourceUrl
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.ressourceUrl = newVal
        this.setItem(newItem)
      }
    }
  },
  methods: {
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator () {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('title', this.item.title, 3, 200, true)
      this.formValidator.checkTextFieldValidity('summary', this.item.summary, 5, 512, true)
      this.formValidator.checkDateFieldValidity('startDate', this.item.startDate, this.minDate, this.startMaxDate, true)
      this.formValidator.checkDateFieldValidity('endDate', this.item.endDate, this.endMinDate, this.maxDate, !this.publisher.context.redactor.optionalPublishTime)
      this.formValidator.checkTextFieldValidity('ressourceUrl', this.item.ressourceUrl, null, 2048, true)
      this.updateItemValidated()
    },
    formatDateToString (date) {
      return DateUtils.convertLocalDateToServer(date)
    },
    formatDateToIntString (date) {
      return DateUtils.formatDateToShortIntString(date, store.getters.getLanguage)
    },
    formatStringToDate (date) {
      return DateUtils.convertLocalDateFromServer(date)
    },
    getUrlFromEnclosure () {
      if (this.item.enclosure) {
        return UploadUtils.getInternalUrl(this.item.enclosure)
      }
    },
    getUrlFromBlob (blob) {
      return URL.createObjectURL(blob)
    },
    onSelectPicUrl () {
      const newContent = Object.assign({}, this.content)
      newContent.picUrl = this.urlInput.value
      this.setContent(newContent)

      // Vérification de l'url sélectionnée
      this.urlInputError = FormValidationUtils.getTextFieldError(this.content.picUrl, null, 2048, false)
      // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
      if (this.urlInputError === null && !this.urlInput.checkValidity()) {
        this.urlInputError = 'invalidurl'
      }
    },
    onSelectFile () {
      const file = this.fileInput.files ? this.fileInput.files[0] : null
      this.fileInputError = null
      if (file) {
        // Vérification de la validatié du fichier sélectionnée
        // 10MB = 10 * 1024 * 1024 B
        this.fileInputError = FormValidationUtils.getFileFieldError(file, 'image/*', 10 * 1024 * 1024, false)

        if (this.fileInputError === null) {
          UploadUtils.getImageDimension(file).then(dimension => {
            if (dimension.width > 240 || dimension.height > 240) {
              UploadUtils.cropImage(file, 240, 240, true).then(img => {
                const newContent = Object.assign({}, this.content)
                newContent.file = img
                this.setContent(newContent)
              })
            } else {
              const newContent = Object.assign({}, this.content)
              newContent.file = file
              this.setContent(newContent)
            }
          })
        } else {
          this.fileInput.value = null
          const newContent = Object.assign({}, this.content)
          newContent.file = null
          this.setContent(newContent)
        }
      }
    },
    updateItemValidated () {
      this.setItemValidated(!this.formValidator.hasError() && !this.invalidRessourceUrl)
    },
    validatePicUrl (picUrl) {
      const newItem = Object.assign({}, this.item)
      newItem.enclosure = picUrl
      this.setItem(newItem)
      this.cropImageModale.hide()
    },
    uploadFile () {
      this.setProgressStatus('success')
      var dataFile = this.content.file
      // we upload cropped file with extension jpg, it's lighter than png
      return this.uploadLinkedFile(dataFile, dataFile.name, true, true, (response, headers) => {
        const location = decodeURIComponent(headers.location)
        if (this.item.id) {
          ItemService.patch({
            objectId: this.item.id,
            attribute: 'enclosure',
            value: location
          }).then(() => {
            const newItem = Object.assign({}, this.item)
            newItem.enclosure = location
            this.setItem(newItem)
            this.cropImageModale.hide()
            this.setProgress(null)
          })
        } else {
          const newItem = Object.assign({}, this.item)
          newItem.enclosure = location
          this.setItem(newItem)
          this.cropImageModale.hide()
          this.setProgress(null)
        }
      })
    }
  },
  mounted () {
    this.urlInput = this.$refs.urlInput
    this.fileInput = this.$refs.fileInput
    this.cropImageModale = new Modal(this.$refs.cropImageModale)
    this.ressourceUrlInput = this.$refs.ressourceUrlInput

    // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
    this.invalidRessourceUrl = !this.formValidator.hasError('ressourceUrl') && !this.ressourceUrlInput.checkValidity()
    this.updateItemValidated()
  },
  created () {
    this.initFormValidator()
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'item.title' (newVal) {
      this.formValidator.checkTextFieldValidity('title', newVal, 3, 200, true)
      this.updateItemValidated()
    },
    'item.summary' (newVal) {
      this.formValidator.checkTextFieldValidity('summary', newVal, 5, 512, true)
      this.updateItemValidated()
    },
    'item.startDate' (newVal) {
      this.formValidator.checkDateFieldValidity('startDate', newVal, this.minDate, this.startMaxDate, true)
      this.formValidator.checkDateFieldValidity('endDate', this.item.endDate, this.endMinDate, this.maxDate, !this.publisher.context.redactor.optionalPublishTime)
      this.updateItemValidated()
    },
    'item.endDate' (newVal) {
      this.formValidator.checkDateFieldValidity('endDate', newVal, this.endMinDate, this.maxDate, !this.publisher.context.redactor.optionalPublishTime)
      this.updateItemValidated()
    },
    'item.ressourceUrl' (newVal) {
      this.formValidator.checkTextFieldValidity('ressourceUrl', newVal, null, 2048, true)
      // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
      this.invalidRessourceUrl = !this.formValidator.hasError('ressourceUrl') && !this.ressourceUrlInput.checkValidity()
      this.updateItemValidated()
    }
  }
}
</script>
