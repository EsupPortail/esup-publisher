<template>
  <div>
    <div class="newsForm">
        <div class="form-group d-none">
            <label class="control-label" for="ID">ID</label>
            <input type="text" class="form-control" name="id" id="ID"
                   v-model="item.id" readonly>
        </div>

        <div class="form-group">
            <label class="control-label" for="title">{{ $t('news.title') }}</label>
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
            <label class="control-label" for="enclosure">{{ $t('news.enclosure') }}</label>
            <div class="form-inline">
                <button type="button" v-if="!item.enclosure" class="btn btn-light btn-file" data-bs-toggle="modal" data-bs-target="#cropImageModale" @click="clearUpload()">
                    <span>{{ $t('news.enclosure-button') }}</span>
                </button>

                <img v-if="item.enclosure" name="enclosure" id="enclosure" :src="getUrlFromEnclosure()" class="img-responsive" />
                <a v-if="item.enclosure" href="" data-bs-toggle="modal" data-bs-target="#deleteEnclosureConfirmation" v-tooltip="$t('entity.action.delete')">
                    <i class="far fa-times-circle text-danger"></i>
                </a>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label" for="summary">{{ $t('news.summary') }}</label>
            <textarea class="form-control" name="summary" id="summary" rows="3" v-model="itemSummary" required minlength="5" maxlength="512" />
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
                <label class="control-label" for="startDate">{{ $t('news.startDate') }}</label>
                <input type="date" class="form-control" name="startDate" id="startDate" placeholder="jj/mm/aaaa" v-model="itemStartDate"
                    required :min="formatDateToString(minDate)" :max="formatDateToString(item.endDate)">
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
                    {{ $t('entity.validation.maxdate', { max: formatDateToIntString(item.endDate)}) }}
                </div>
            </div>
            <div class="form-group col-md-4">
                <label class="control-label" for="endDate">{{ $t('news.endDate') }}</label>
                <span v-if="publisher.context.redactor.optionalPublishTime" class="d-block d-md-none">{{ $t('news.detail.unlimitedEndDate') }}</span>
                <input type="date" class="form-control" name="endDate" id="endDate" placeholder="jj/mm/aaaa" v-model="itemEndDate"
                  :min="formatDateToString(endMinDate)" :max="formatDateToString(maxDate)"
                  v-tooltip="publisher.context.redactor.optionalPublishTime ? $t('news.detail.unlimitedEndDate') : ''"
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
            <div class="form-group col-md-4">
                <label class="control-label me-2" for="rssAllowed">{{ $t('news.rssAllowed') }}</label>
                <input type="checkbox" class="form-check-input" name="rssAllowed" id="rssAllowed" v-model="itemRssAllowed" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label" for="body">{{ $t('news.body') }}</label>
            <RichText v-model="itemBody" :entityId="publisher.context.organization.id"
              :imageSizeMax="imageSizeMax" :fileSizeMax="fileSizeMax"
              :errorImageSizeMsg="errorImageSizeMsg" :errorFileSizeMsg="errorFileSizeMsg"
              :callBackSuccess="fileUploadedInBodySuccess" :callBackError="fileUploadedInBodyError"
              :callBackProgress="fileUploadedInBodyProgress"></RichText>
            <div class="invalid-feedback d-block"
                  v-if="formValidator.hasError('body', formErrors.REQUIRED)">
                {{ $t('entity.validation.required') }}
            </div>
            <div class="invalid-feedback d-block"
                v-if="formValidator.hasError('body', formErrors.MIN_LENGTH)">
                {{ $t('entity.validation.minlength', {min: '15'}) }}
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

    <div class="modal fade" id="cropImageModale" tabindex="-1" role="dialog" aria-labelledby="myCropImageModale" aria-hidden="true" ref="cropImageModale">
        <div class="modal-dialog modal-fullscreen">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">{{ $t('news.inputfile.title') }}</h3>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"></button>
                </div>
                <div class="modal-body">
                    <form name="publishContentEnclosureForm" role="form" novalidate class="panel-group">
                        <div class="card">
                            <div class="inputArea form-group">
                                <h3>{{ $t('news.inputfile.upload-label') }}</h3>&nbsp;
                                <button type="button" class="btn btn-primary btn-file" name="fileEnclosure" @click="clearUpload();fileInput.click()">
                                  <i class="fas fa-folder-open"></i>&nbsp;<span>{{ $t('news.inputfile.upload-button') }}</span>
                                </button>
                                <input type="file" id="file" name="file" ref="fileInput" accept="image/*" @change="selectFile()" hidden/>
                                <p class="help-block"
                                    v-if="fileInputError === formErrors.PATTERN">
                                    {{ $t('entity.validation.filepattern') }}
                                </p>
                                <p class="help-block"
                                    v-if="fileInputError === formErrors.MAX_SIZE">
                                    {{ $t('entity.validation.maxsize', {maxsize: '10MB'}) }}
                                </p>
                            </div>

                            <div v-if="content.file && content.dataUrl" class="cropArea">
                                <vue-cropper
                                  ref="cropper"
                                  :src="content.dataUrl"
                                  :aspect-ratio="cropperConf.ratio"
                                  :view-mode="cropperConf.viewMode"
                                  preview=".previewCropArea">
                                </vue-cropper>
                            </div>
                            <div v-if="content.file && content.dataUrl" class="previewCropArea"
                              :style="{'width': cropperConf.size.w + 'px', 'max-width': '100%', 'height': cropperConf.size.h + 'px', 'max-height': '100%'}"></div>
                        </div>
                        <div class="progress" v-if="progress > 0">
                            <div :class="'progress-bar progress-bar-striped bg-' + progressStatus" role="progressbar"
                                 :aria-valuenow="progress" aria-valuemin="0"
                                 aria-valuemax="100" :style="{'width': progress+'%'}">
                                <span class="progress-text">{{progress}}%</span>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" @click="uploadFile()" class="btn btn-primary" :disabled="!content.file || !content.dataUrl">
                        <span class="fas fa-download"></span>&nbsp;<span>{{ $t('entity.action.save') }}</span></button>
                    <button type="button" class="btn btn-default btn-outline-dark" @click="clearUpload()" :disabled="!content.file || !content.dataUrl">
                        <span class="fas fa-ban"></span>&nbsp;<span>{{ $t('entity.action.reset') }}</span>
                    </button>
                    <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                        <span class="fas fa-ban"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
                    </button>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script>
import ItemService from '@/services/entities/item/ItemService'
import UploadUtils from '@/services/util/UploadUtils'
import DateUtils from '@/services/util/DateUtils'
import CommonUtils from '@/services/util/CommonUtils'
import { FormValidationUtils, FormErrorType } from '@/services/util/FormValidationUtils'
import store from '@/store/index.js'
import i18n from '@/i18n'
import { Modal } from 'bootstrap'
import VueCropper from 'vue-cropperjs'
import 'cropperjs/dist/cropper.css'
import RichText from '@/components/richtext/RichText'

const { t } = i18n.global

export default {
  name: 'ContentNews',
  components: {
    VueCropper,
    RichText
  },
  data () {
    return {
      fileInput: null,
      fileInputError: null,
      cropImageModale: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
      errorImageSizeMsg: null,
      errorFileSizeMsg: null
    }
  },
  inject: [
    'publisher', 'item', 'setItem', 'progress', 'setProgress', 'progressStatus', 'setProgressStatus', 'content', 'setContent',
    'clearUpload', 'uploadLinkedFile', 'cropperConf', 'setItemValidated', 'imageSizeMax', 'fileSizeMax', 'linkedFilesToContent',
    'setLinkedFilesToContent', 'manageUploadError', 'minDate', 'endMinDate', 'maxDate'
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
    itemBody: {
      get () {
        return this.item.body
      },
      set (newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.body = newVal
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
      this.formValidator.checkDateFieldValidity('startDate', this.item.startDate, this.minDate, this.item.endDate, true)
      this.formValidator.checkDateFieldValidity('endDate', this.item.endDate, this.endMinDate, this.maxDate, !this.publisher.context.redactor.optionalPublishTime)
      this.formValidator.checkTextFieldValidity('body', CommonUtils.removeHtmlTags(this.item.body), 15, null, true)
      this.updateItemValidated()
    },
    selectFile () {
      const newContent = Object.assign({}, this.content)
      newContent.file = this.fileInput.files ? this.fileInput.files[0] : ''
      this.setContent(newContent)

      // Vérification de la validatié du fichier sélectionnée
      // 10MB = 10 * 1024 * 1024 B
      this.fileInputError = FormValidationUtils.getFileFieldError(this.content.file, 'image/*', 10 * 1024 * 1024, false)

      // Génération d'une URL pour l'image sélectionnée si valide
      if (this.content.file && this.fileInputError === null) {
        const reader = new FileReader()
        reader.onload = (event) => {
          const newContent = Object.assign({}, this.content)
          newContent.dataUrl = event.target.result
          this.setContent(newContent)
        }
        reader.readAsDataURL(this.content.file)
      }
    },
    uploadFile () {
      const dataUrl = this.$refs.cropper.getCroppedCanvas({
        width: this.cropperConf.size.w,
        height: this.cropperConf.size.h,
        imageSmoothingQuality: this.cropperConf.quality
      }).toDataURL(this.cropperConf.format)

      this.setProgressStatus('success')
      var dataFile = (typeof dataUrl !== 'undefined') ? UploadUtils.dataUrltoBlob(dataUrl, this.content.file.name.substr(0, this.content.file.name.lastIndexOf('.')) + '.jpg') : this.content.file
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
    },
    getUrlFromEnclosure () {
      if (this.item.enclosure) {
        return process.env.VUE_APP_BACK_BASE_URL + this.item.enclosure
      }
    },
    formatDateToString (date) {
      return DateUtils.convertLocalDateToServer(date)
    },
    formatDateToIntString (date) {
      return DateUtils.convertToIntString(date, {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      }, store.getters.getLanguage)
    },
    formatStringToDate (date) {
      return DateUtils.convertLocalDateFromServer(date)
    },
    updateItemValidated () {
      this.setItemValidated(!this.formValidator.hasError())
    },
    fileUploadedInBodySuccess (file, url) {
      var newLinks = Array.from(this.linkedFilesToContent || [])
      newLinks.push({
        uri: url,
        filename: file.name,
        inBody: true,
        contentType: file.type
      })
      this.setLinkedFilesToContent(newLinks)
      this.setProgress(null)
    },
    fileUploadedInBodyError (response) {
      if (CommonUtils.isString(response)) {
        this.$toast.warning(response)
      } else {
        this.manageUploadError(response)
      }
    },
    fileUploadedInBodyProgress (evt) {
      this.setProgressStatus('success')
      this.setProgress(Math.min(100, parseInt(100.0 * evt.loaded / evt.total)))
    }
  },
  mounted () {
    this.fileInput = this.$refs.fileInput
    this.cropImageModale = new Modal(this.$refs.cropImageModale)
  },
  created () {
    this.errorImageSizeMsg = t('errors.upload.filesize', { size: CommonUtils.convertByteToDisplayedString(this.imageSizeMax, 2) })
    this.errorFileSizeMsg = t('errors.upload.filesize', { size: CommonUtils.convertByteToDisplayedString(this.fileSizeMax, 2) })

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
      this.formValidator.checkDateFieldValidity('startDate', newVal, this.minDate, this.item.endDate, true)
      this.formValidator.checkDateFieldValidity('endDate', this.item.endDate, this.endMinDate, this.maxDate, !this.publisher.context.redactor.optionalPublishTime)
      this.updateItemValidated()
    },
    'item.endDate' (newVal) {
      this.formValidator.checkDateFieldValidity('startDate', this.item.startDate, this.minDate, newVal, true)
      this.formValidator.checkDateFieldValidity('endDate', newVal, this.endMinDate, this.maxDate, !this.publisher.context.redactor.optionalPublishTime)
      this.updateItemValidated()
    },
    'item.body' (newVal) {
      this.formValidator.checkTextFieldValidity('body', CommonUtils.removeHtmlTags(newVal), 15, null, true)
      this.updateItemValidated()
    }
  }
}
</script>
