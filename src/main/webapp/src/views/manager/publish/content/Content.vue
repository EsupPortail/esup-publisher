<template>
  <div class="publish-content">
    <h3>{{ $t('manager.publish.content.title') }}</h3>

    <div class="form-group row" v-if="itemTypeList.length > 1">
      <label class="col-md-4 control-label">{{ $t('manager.publish.content.type') }}</label>
      <div class="col-md-8" v-if="(item !== null && item !== undefined && item.id) || itemTypeList.length == 1">
        <div class="form-check form-check-inline">
          <label class="form-check-label"
            ><span>{{ $t('enum.itemType.' + content.type) }}</span></label
          >
        </div>
      </div>
      <div class="col-md-8" v-if="(item === null || item === undefined || !item.id) && itemTypeList.length > 1">
        <div class="form-check form-check-inline" v-for="itemType in itemTypeList" :key="itemType">
          <input
            class="form-check-input"
            type="radio"
            name="type"
            :id="'itemType' + itemType"
            :value="itemType"
            v-model="content.type"
            @change="changeContentType('{{content.type}}')"
          />
          <label class="form-check-label fw-normal" :for="'itemType' + itemType"
            ><span>{{ $t('enum.itemType.' + itemType) }}</span></label
          >
        </div>
      </div>
    </div>

    <form name="publishContentForm" class="was-validated" novalidate>
      <div v-if="content.type === 'NEWS'">
        <ContentNews></ContentNews>
      </div>
      <div v-else-if="content.type === 'MEDIA'">
        <ContentMedia></ContentMedia>
      </div>
      <div v-else-if="content.type === 'RESOURCE'">
        <ContentResource></ContentResource>
      </div>
      <div v-else-if="content.type === 'FLASH'">
        <ContentFlash></ContentFlash>
      </div>
      <div v-else-if="content.type === 'ATTACHMENT'">
        <ContentAttachment></ContentAttachment>
      </div>
    </form>

    <div class="text-center">
      <div class="btn-group" role="group">
        <router-link to="classification" custom v-slot="{ navigate }" v-if="item === null || item.type !== 'FLASH'">
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-arrow-left"></span>&nbsp;<span> {{ $t('entity.action.back') }}</span>
          </button>
        </router-link>
        <router-link to="publisher" custom v-slot="{ navigate }" v-if="item !== null && item.type === 'FLASH'">
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-arrow-left"></span>&nbsp;<span> {{ $t('entity.action.back') }}</span>
          </button>
        </router-link>

        <router-link to="/home" custom v-slot="{ navigate }">
          <button
            type="button"
            class="btn btn-default btn-outline-dark btn-nav"
            @click="
              cancel();
              navigate();
            "
          >
            <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
          </button>
        </router-link>

        <router-link to="targets" custom v-slot="{ navigate }" v-if="goOnTargets()">
          <button type="button" :disabled="!itemValidated" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span> {{ $t('manager.publish.nextStep') }} </span>&nbsp;<span class="fas fa-arrow-right"></span>
          </button>
        </router-link>
      </div>
    </div>

    <div class="modal fade" id="deleteEnclosureConfirmation" ref="deleteEnclosureConfirmation">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title">{{ $t('entity.delete.title') }}</h4>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"></button>
          </div>
          <div class="modal-body">
            <p>{{ $t('news.delete.enclosure') }}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
              <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button type="submit" class="btn btn-danger" @click="removeEnclosure()">
              <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="deleteAttachmentConfirmation" ref="deleteAttachmentConfirmation">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title">{{ $t('entity.delete.title') }}</h4>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"></button>
          </div>
          <div class="modal-body">
            <p>
              {{
                $t('attachment.delete.question', {
                  id: attachmentToDel ? attachmentToDel.filename : '',
                })
              }}
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
              <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button type="submit" class="btn btn-danger" @click="confirmDeleteAttachment()">
              <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { computed, readonly } from 'vue';
import FileManagerService from '@/services/entities/file/FileManagerService';
import ConfigurationService from '@/services/params/ConfigurationService';
import EnumDatasService from '@/services/entities/enum/EnumDatasService';
import ItemService from '@/services/entities/item/ItemService';
import Base64Utils from '@/services/util/Base64Utils';
import CommonUtils from '@/services/util/CommonUtils';
import DateUtils from '@/services/util/DateUtils';
import UploadUtils from '@/services/util/UploadUtils';
import ContentAttachment from './ContentAttachment';
import ContentFlash from './ContentFlash';
import ContentMedia from './ContentMedia';
import ContentNews from './ContentNews';
import ContentResource from './ContentResource';
import i18n from '@/i18n';
import { Modal } from 'bootstrap';

const { t } = i18n.global;

export default {
  name: 'Content',
  components: {
    ContentAttachment,
    ContentFlash,
    ContentMedia,
    ContentNews,
    ContentResource,
  },
  data() {
    return {
      itemTypeList: [],
      content: { type: '', file: '', dataUrl: '', picUrl: '' },
      dtformats: [],
      dtformat: null,
      today: null,
      minDate: null,
      endMinDate: null,
      defaultMaxDuration: null,
      maxDate: null,
      startMaxDate: null,
      cropperConf: null,
      defaultCropperConf: {
        size: { w: 240, h: 240 },
        ratio: 1,
        quality: 'high',
        format: 'image/jpeg',
        viewMode: 1,
      },
      invalidFiles: [],
      mediaType: '',
      progress: null,
      progressStatus: null,
      attachmentToDel: null,
      deleteEnclosureModal: null,
      deleteAttachmentModal: null,
    };
  },
  provide() {
    return {
      progress: readonly(computed(() => this.progress)),
      setProgress: (progress) => {
        this.progress = progress;
      },
      progressStatus: readonly(computed(() => this.progressStatus)),
      setProgressStatus: (progressStatus) => {
        this.progressStatus = progressStatus;
      },
      content: readonly(computed(() => this.content)),
      setContent: (content) => {
        this.content = content;
      },
      clearUpload: this.clearUpload,
      uploadLinkedFile: this.uploadLinkedFile,
      cropperConf: readonly(computed(() => this.cropperConf)),
      setItemValidated: this.setItemValidated,
      imageSizeMax: readonly(computed(() => this.imageSizeMax)),
      fileSizeMax: readonly(computed(() => this.fileSizeMax)),
      authorizedMimeTypes: readonly(computed(() => this.authorizedMimeTypes)),
      manageUploadError: this.manageUploadError,
      deleteAttachment: this.deleteAttachment,
      minDate: readonly(computed(() => this.minDate)),
      endMinDate: readonly(computed(() => this.endMinDate)),
      maxDate: readonly(computed(() => this.maxDate)),
      startMaxDate: readonly(computed(() => this.startMaxDate)),
      setItem: (newItem) => {
        if (
          CommonUtils.isDefined(newItem) &&
          CommonUtils.isDefined(newItem.type) &&
          (!CommonUtils.isDefined(this.item) ||
            this.item == null ||
            newItem.type !== this.item.type ||
            newItem.startDate !== this.item.startDate)
        ) {
          this.updateMaxDate(newItem);
        }
        this.setItem(newItem);
      },
    };
  },
  inject: [
    'contentData',
    'publisher',
    'setPublisher',
    'item',
    'setItem',
    'highlight',
    'linkedFilesToContent',
    'setLinkedFilesToContent',
    'itemValidated',
    'setItemValidated',
  ],
  computed: {
    itemStatusList() {
      return EnumDatasService.getItemStatusList();
    },
    imageSizeMax() {
      return ConfigurationService.getConfUploadImageSize();
    },
    fileSizeMax() {
      return ConfigurationService.getConfUploadFileSize();
    },
    authorizedMimeTypes() {
      return ConfigurationService.getConfAuthorizedMimeTypes();
    },
  },
  methods: {
    initItem() {
      var entityID = this.publisher.context.organization.id;
      var redactorID = this.publisher.context.redactor.id;

      // tomorrow isn't more tomorrow as param should be 1 instead of 0
      var tomorrow = DateUtils.addDaysToLocalDate(this.today, 0);
      // warning should be < $scope.defaultMaxDuration
      var next4weeks = DateUtils.addDaysToLocalDate(this.today, 28);

      this.setItem({});

      this.initCropper();

      // generic item
      const item = {
        title: null,
        enclosure: null,
        endDate: this.publisher.context.redactor.optionalPublishTime ? null : next4weeks,
        startDate: tomorrow,
        validatedBy: null,
        validatedDate: null,
        status: null,
        summary: null,
        rssAllowed: false,
        highlight: this.highlight,
        createdBy: null,
        createdDate: null,
        lastModifiedBy: null,
        lastModifiedDate: null,
        id: null,
        organization: { id: entityID },
        redactor: { id: redactorID },
      };

      switch (this.content.type) {
        case 'NEWS':
          item.type = 'NEWS';
          item.body = null;
          break;
        case 'MEDIA':
          item.type = 'MEDIA';
          break;
        case 'RESOURCE':
          item.type = 'RESOURCE';
          item.ressourceUrl = null;
          break;
        case 'FLASH':
          item.type = 'FLASH';
          item.body = null;
          item.endDate = this.publisher.context.redactor.optionalPublishTime ? null : DateUtils.addDaysToLocalDate(this.today, 14);
          break;
        case 'ATTACHMENT':
          item.type = 'ATTACHMENT';
          break;
        default:
          throw new Error('Type not managed : ' + this.content.type);
      }
      this.setItem(item);
    },
    initCropper() {
      switch (this.content.type) {
        case 'NEWS':
          this.cropperConf.size = { w: 240, h: 240 };
          this.cropperConf.ratio = 1;
          break;
        case 'FLASH':
          this.cropperConf.size = { w: 800, h: 240 };
          this.cropperConf.ratio = 3.3;
          break;
        default:
          this.cropperConf = Object.assign({}, this.defaultCropperConf);
          break;
      }
    },
    updateMinDate(item) {
      if (
        CommonUtils.isDefined(item) &&
        CommonUtils.isDefined(item.type) &&
        CommonUtils.isDefined(item.id) &&
        item.id !== null &&
        CommonUtils.isDefined(item.startDate) &&
        !(CommonUtils.isDefined(item.endDate) && item.endDate !== null && DateUtils.getDateDifference(item.endDate, this.today) > 0)
      ) {
        this.minDate = DateUtils.min(item.startDate, this.today);
      }
    },
    updateMaxDate(item) {
      if (CommonUtils.isDefined(item) && CommonUtils.isDefined(item.type) && CommonUtils.isDefined(item.startDate)) {
        this.maxDate = DateUtils.addDaysToLocalDate(item.startDate, this.defaultMaxDuration);
        this.endMinDate = item.startDate;
      }
    },
    changeContentType(oldValue) {
      if (!CommonUtils.equals(oldValue, this.content.type) || !CommonUtils.isDefined(this.item) || CommonUtils.equals({}, this.item)) {
        this.initItem();
      } else {
        this.initCropper();
      }
    },
    cancel() {
      // on cancel we try to remove enclosure only on new item (without id)
      if (
        !CommonUtils.isDefined(this.item.enclosure) ||
        this.item.enclosure === null ||
        this.item.enclosure === undefined ||
        !CommonUtils.isDefined(this.item.id)
      ) {
        return;
      }
      this.deleteEnclosure();
    },
    deleteEnclosure() {
      FileManagerService.delete(this.publisher.context.organization.id, true, Base64Utils.encode(this.item.enclosure)).then(() => {
        if (this.item.id) {
          ItemService.patch({
            objectId: this.item.id,
            attribute: 'enclosure',
            value: null,
          }).then(() => {
            this.clearUpload();
            const newItem = Object.assign({}, this.item);
            newItem.enclosure = null;
            this.setItem(newItem);
            this.deleteEnclosureModal.hide();
          });
        } else {
          this.clearUpload();
          const newItem = Object.assign({}, this.item);
          newItem.enclosure = null;
          this.setItem(newItem);
          this.deleteEnclosureModal.hide();
        }
      });
    },
    deleteAttachment(attachment) {
      this.attachmentToDel = attachment;
      this.deleteAttachmentModal.show();
    },
    confirmDeleteAttachment() {
      FileManagerService.delete(this.publisher.context.organization.id, false, Base64Utils.encode(this.attachmentToDel.uri)).then(() => {
        this.setLinkedFilesToContent(
          this.linkedFilesToContent.filter((val) => {
            return val.uri !== this.attachmentToDel.uri;
          }),
        );
        if (this.item.id) {
          ItemService.patch({
            objectId: this.item.id,
            attribute: 'attachment',
            value: this.attachmentToDel.uri,
          }).then(() => {
            this.deleteAttachmentModal.hide();
          });
        } else {
          this.deleteAttachmentModal.hide();
        }
      });
    },
    clearUpload() {
      this.content.file = '';
      this.content.dataUrl = '';
      this.content.picUrl = '';
      this.progress = null;
      document.querySelectorAll("input[type='file']").forEach((el) => {
        el.value = null;
      });
    },
    goOnTargets() {
      return this.publisher !== null && this.publisher !== undefined && this.publisher.context.redactor.writingMode === 'TARGETS_ON_ITEM';
    },
    uploadLinkedFile(file, filename, isImage, isPublic, successCallBack) {
      var maxSize = isImage ? this.imageSizeMax : this.fileSizeMax;
      if (file.size <= maxSize) {
        this.progressStatus = 'success';
        UploadUtils.upload(
          'app/upload/',
          {
            file: file,
            isPublic: isPublic,
            entityId: this.publisher.context.organization.id,
            name: filename,
          },
          (response, headers) => {
            successCallBack(response, headers);
          },
          (response) => {
            // ERROR
            this.manageUploadError(response);
          },
          (evt) => {
            this.progress = Math.min(100, parseInt((100.0 * evt.loaded) / evt.total));
          },
        );
        return true;
      }
      // FILE SIZE ISSUE - useless as the server control it, but avoid a useless request
      this.$toast.warning(
        t('errors.upload.filesize', {
          size: CommonUtils.convertByteToDisplayedString(maxSize, 2),
        }),
      );
      return true;
    },
    manageUploadError(response) {
      if (response.status > 0) {
        this.progressStatus = 'warning';
        if (response.message) {
          var params = {};
          if (response.params) {
            params = Object.assign({}, response.params);
            if (response.params.size) {
              params.size = CommonUtils.convertByteToDisplayedString(response.params.size, 2);
            }
          }
          params.code = response.status;
          this.$toast.warning(t(response.message, params));
        } else {
          this.$toast.warning(t('errors.upload.generic', { code: response.status }));
        }
      } else if (response.status === 0) {
        this.progressStatus = 'warning';
        this.$toast.warning(t('errors.upload.ERR_INTERNET_DISCONNECTED'));
      }
      setTimeout(() => {
        this.progress = null;
      }, 3000);
    },
    removeEnclosure() {
      if (!CommonUtils.isDefined(this.item.enclosure) || this.item.enclosure == null) return;
      this.deleteEnclosure();
    },
  },
  mounted() {
    this.deleteEnclosureModal = new Modal(this.$refs.deleteEnclosureConfirmation);
    this.deleteAttachmentModal = new Modal(this.$refs.deleteAttachmentConfirmation);
  },
  created() {
    // Si aucun publisher de saisie, on redirige vers la page Publisher
    if (this.publisher === null || this.publisher === undefined || !this.publisher.id) {
      this.$router.push({ path: 'publisher' });
    } else {
      this.cropperConf = Object.assign({}, this.defaultCropperConf);
      this.itemTypeList = this.publisher.context.reader.authorizedTypes;

      this.dtformats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'dd/MM/yyyy', 'shortDate', 'yyyy-MM-dd'];
      this.dtformat = this.dtformats[6];

      this.today = DateUtils.addDaysToLocalDate(new Date(), 0);
      // init default max and min date;
      this.minDate = DateUtils.addDaysToLocalDate(this.today, 0);
      this.endMinDate = DateUtils.addDaysToLocalDate(this.today, 0);
      this.defaultMaxDuration =
        this.publisher.context.redactor.nbDaysMaxDuration > 0 ? this.publisher.context.redactor.nbDaysMaxDuration : 365;
      this.maxDate = DateUtils.addDaysToLocalDate(this.today, this.defaultMaxDuration);
      // Pas de limite supérieure à la date de début
      this.startMaxDate = null;

      if (
        (this.item === null || this.item === undefined || CommonUtils.equals({}, this.item)) &&
        this.contentData &&
        this.contentData.item
      ) {
        const item = Object.assign({}, this.contentData.item);
        this.updateMinDate(this.contentData.item);
        this.updateMaxDate(this.contentData.item);
        item.highlight = this.highlight;
        // update publisher information when modified
        item.organization = Object.assign({}, this.publisher.context.organization);
        item.redactor = Object.assign({}, this.publisher.context.redactor);
        this.setItem(item);
      } else if (this.item) {
        this.updateMinDate(this.item);
        this.updateMaxDate(this.item);
      } else {
        this.setItem({});
      }
      if (
        (this.linkedFilesToContent === null || this.linkedFilesToContent === undefined || this.linkedFilesToContent.length === 0) &&
        this.contentData !== null &&
        this.contentData !== undefined &&
        this.contentData.linkedFiles
      ) {
        this.setLinkedFilesToContent(Array.from(this.contentData.linkedFiles));
      }

      if (this.item.type) {
        this.content.type = this.item.type;
      }

      if (this.itemTypeList.length === 1) {
        var oldVal = this.content.type;
        this.content.type = this.itemTypeList[0];
        this.changeContentType(oldVal);
      }
    }
  },
};
</script>
