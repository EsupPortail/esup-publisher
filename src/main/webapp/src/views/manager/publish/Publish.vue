<template>
  <div class="publish-form-container">
    <div class="publish-header">
      <h2>{{ $t("manager.publish.title") }}</h2>

      <div class="status-buttons">
        <div class="publish-form-views-steps" v-if="publishingType === 'NONE'">
          <router-link to="publisher"><span class="step-number">1</span><span class="step-label"> {{ $t("manager.publish.publisher.step") }}</span></router-link>
        </div>
        <div class="publish-form-views-steps" v-else-if="publishingType === 'STATIC'">
          <router-link to="publisher"><span class="step-number">1</span><span class="step-label"> {{ $t("manager.publish.publisher.step") }}</span></router-link>
          <router-link to="content" v-disable-click="!isPublisherSelected"><span class="step-number">2</span><span class="step-label"> {{ $t("manager.publish.content.step") }}</span></router-link>
        </div>
        <div class="publish-form-views-steps" v-else>
          <router-link to="publisher"><span class="step-number">1</span><span class="step-label"> {{ $t("manager.publish.publisher.step") }}</span></router-link>
          <router-link to="classification" v-disable-click="!isPublisherSelected"><span class="step-number">2</span><span class="step-label"> {{ $t("manager.publish.classification.step") }}</span></router-link>
          <router-link to="content" v-disable-click="!isClassificationsSelected"><span class="step-number">3</span><span class="step-label"> {{ $t("manager.publish.content.step") }}</span></router-link>
          <router-link to="targets" v-disable-click="!isItemValidated"><span class="step-number">4</span><span class="step-label"> {{ $t("manager.publish.targets.step") }}</span></router-link>
        </div>
      </div>
    </div>

    <div class="publish-body" id="publish-form-views" v-if="dataLoaded">
      <router-view></router-view>
    </div>

    <div class="modal fade" id="saveDraftConfirmation" ref="confirmSaveModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form name="saveDraftForm">
                    <div class="modal-header">
                      <h4 class="modal-title">{{ $t("item.action.confirmsave.title") }}</h4>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"></button>
                    </div>
                    <div class="modal-body">
                        <i18n-t keypath="item.action.confirmsave.question" tag="p" scope="global">
                          <template v-slot:warning>
                            <br/>
                            <br/>
                            <div class="alert alert-warning">{{ $t('item.action.confirmsave.warning') }}</div>
                          </template>
                        </i18n-t>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                            <span class="far fa-times-circle"></span>&nbsp;<span>{{ $t("entity.action.cancel") }}</span>
                        </button>
                        <button type="button" class="btn btn-primary" @click="publishItem('DRAFT')">
                            <span class="far fa-check-circle"></span>&nbsp;<span>{{ $t("entity.action.validate") }}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="publish-footer text-end" v-if="canSave">
        <div class="btn-group" role="group">
            <button type="button" class="btn btn-primary" @click="confirmSave()">
                <span class="fas fa-download"></span>&nbsp;<span>{{ $t("item.action.save") }}</span>
            </button>
            <button type="button" class="btn btn-primary" v-if="canSubmit" @click="publishItem('PUBLISH')">
                <span class="fas fa-paper-plane"></span>&nbsp;<span>{{ $t("item.action.publish") }}</span>
            </button>
        </div>
    </div>
  </div>
</template>
<script>
import { computed, readonly } from 'vue'
import { Modal } from 'bootstrap'
import ContentService from '@/services/entities/content/ContentService'

export default {
  name: 'Publish',
  data () {
    return {
      dataLoaded: false,
      contentData: null,
      publisher: null,
      item: null,
      classifications: null,
      targets: null,
      linkedFilesToContent: null,
      itemValidated: false,
      highlight: false,
      confirmSaveModal: null
    }
  },
  provide () {
    return {
      contentData: readonly(computed(() => this.contentData)),
      publisher: readonly(computed(() => this.publisher)),
      setPublisher: (publisher) => { this.publisher = publisher },
      item: readonly(computed(() => this.item)),
      setItem: (item) => { this.item = item },
      classifications: readonly(computed(() => this.classifications)),
      setClassifications: (classifications) => { this.classifications = classifications },
      targets: readonly(computed(() => this.targets)),
      setTargets: (targets) => { this.targets = targets },
      linkedFilesToContent: readonly(computed(() => this.linkedFilesToContent)),
      setLinkedFilesToContent: (linkedFilesToContent) => { this.linkedFilesToContent = linkedFilesToContent },
      itemValidated: readonly(computed(() => this.itemValidated)),
      setItemValidated: (itemValidated) => { this.itemValidated = itemValidated },
      highlight: readonly(computed(() => this.highlight)),
      setHighlight: (highlight) => { this.highlight = highlight }
    }
  },
  computed: {
    publishingType () {
      if (!this.isPublisherSelected) {
        return 'NONE'
      }
      return this.publisher.context.redactor.writingMode
    },
    isPublisherSelected () {
      return this.publisher && this.publisher !== {} && this.publisher.id
    },
    isItemValidated () {
      return this.itemValidated
    },
    isClassificationsSelected () {
      return this.classifications && this.classifications.length > 0
    },
    canSave () {
      // to be able to save an item at least all mandatory attributes should be setted
      return this.publisher && this.publisher.id && this.classifications &&
        this.classifications.length > 0 && this.item && this.item.title && this.itemValidated
    },
    canSubmit () {
      return this.publisher && this.publisher.id && this.itemValidated &&
        this.classifications && this.classifications.length > 0 &&
        (this.publisher.context.redactor.writingMode === 'STATIC' || (this.targets && this.targets.length > 0))
    }
  },
  methods: {
    confirmSave () {
      this.confirmSaveModal.show()
    },
    publishItem (status) {
      switch (status) {
        case 'PUBLISH':
          this.item.status = 'PUBLISHED'
          break
        default:
          // DRAFT
          this.item.status = 'DRAFT'
          break
      }
      var tmpTargets = []

      if (this.targets) {
        for (var i = 0; i < this.targets.length; ++i) {
          tmpTargets.push({
            subject: {
              modelId: this.targets[i].modelId,
              displayName: null,
              foundOnExternalSource: null
            },
            subscribeType: 'FORCED'
          })
        }
      }

      var content = {
        classifications: this.classifications,
        item: this.item,
        targets: tmpTargets,
        linkedFiles: this.linkedFilesToContent
      }
      if (content.item.id != null) {
        ContentService.update(content).then(response => {
          this.confirmSaveModal.hide()
          this.$router.push({ name: 'ContentsOwned', params: { itemState: response.data.value.name } })
        })
      } else {
        ContentService.save(content).then(response => {
          this.confirmSaveModal.hide()
          this.$router.push({ name: 'ContentsOwned', params: { itemState: response.data.value.name } })
        })
      }
    }
  },
  created () {
    // Si on essaye d'accéder à la page parente seulement, on redirige vers la page d'accueil
    if (this.$router.currentRoute.value.path === '/publish') {
      this.$router.push({ name: 'Home' })
    } else {
      if (this.$route.params.id) {
        ContentService.get(this.$route.params.id).then(response => {
          this.contentData = response.data
          this.dataLoaded = true
        })
      } else {
        this.dataLoaded = true
      }
    }
  },
  mounted () {
    // Récupération de la modale de confirmation de sauvegarde
    this.confirmSaveModal = new Modal(this.$refs.confirmSaveModal)
  }
}
</script>
