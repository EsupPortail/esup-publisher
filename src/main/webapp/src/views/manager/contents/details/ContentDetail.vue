<template>
  <div class="header">
      <ul class="nav nav-pills">
          <li class="nav-item" :class="{'active': activeNav === 'content'}">
            <a class="nav-link" :class="{'active': activeNav === 'content'}" @click.prevent="showNav('content')" href=""><span>{{$t('manager.contents.details.item')}}</span></a>
          </li>
          <li class="nav-item" :class="{'active': activeNav === 'publishers'}">
            <a class="nav-link" :class="{'active': activeNav === 'publishers'}" @click.prevent="showNav('publishers')" href=""><span>{{$t('manager.contents.details.publishers')}}</span></a>
          </li>
          <li class="nav-item" v-if="item.redactor.writingMode !== 'STATIC'" :class="{'active': activeNav === 'targets'}">
            <a class="nav-link" :class="{'active': activeNav === 'targets'}" @click.prevent="showNav('targets')" href=""><span>{{$t('manager.contents.details.targets')}}</span></a>
          </li>
      </ul>
  </div>
  <SubjectDetail ref="subjectDetail"></SubjectDetail>
  <div class="tab-content">
      <div id="content" class="tab-pane fade show" :class="{'active': activeNav === 'content'}">
        <div v-if="item.type === 'NEWS'">
          <ContentDetailNews></ContentDetailNews>
        </div>
        <div v-else-if="item.type === 'MEDIA'">
          <ContentDetailMedia></ContentDetailMedia>
        </div>
        <div v-else-if="item.type === 'RESOURCE'">
          <ContentDetailRessource></ContentDetailRessource>
        </div>
        <div v-else-if="item.type === 'FLASH'">
          <ContentDetailFlash></ContentDetailFlash>
        </div>
        <div v-else-if="item.type === 'ATTACHMENT'">
          <ContentDetailAttachment></ContentDetailAttachment>
        </div>
    </div>
    <div id="publishers" class="tab-pane fade show" :class="{'active': activeNav === 'publishers'}">
      <h3>{{$t("manager.contents.details.publishers")}}</h3>
      <div v-for="context in pubContexts" :key="context.id" class="card">
        <div class="card-header">
          <h4 class="card-title">{{context.publisher.context.reader.displayName}} - {{context.publisher.context.redactor.displayName}}</h4>
        </div>
        <div class="card-body">
          <p><span>{{$t("manager.contents.details.publisher.reader")}}</span> {{context.publisher.context.reader.description}}</p>
          <p><span>{{$t("manager.contents.details.publisher.redactor")}}</span> {{context.publisher.context.redactor.description}}</p>
        </div>
        <div class="card-footer" v-if="context.classifications.length > 0">
          <h4>{{$t("manager.contents.details.classifications")}}</h4>
          <ul class="list-group">
            <li v-for="classification in context.classifications" :key="classification.id" class="list-group-item">
              <figure :class="classification.type">
                <img v-if="context.publisher.context.reader.classificationDecorations.includes('ENCLOSURE') && classification.iconUrl" :src="classification.iconUrl" :alt="classification.name" />
                <figcaption>
                  <span class="pastille me-1" :style="{'background-color': context.publisher.context.reader.classificationDecorations.includes('COLOR')?classification.color:null}"
                    v-if='context.publisher.context.reader.classificationDecorations.includes("COLOR")'></span>
                  <span class="text">{{classification.name}}</span>
                </figcaption>
              </figure>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div id="targets" class="tab-pane fade show" :class="{'active': activeNav === 'targets'}">
        <h3>{{$t('manager.contents.details.targets')}}</h3>
        <div v-if="item.redactor.writingMode === 'STATIC'" class="table-responsive table-responsive-to-flexbox">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>{{$t('subscriber.subscribeType')}}</th>
                <th>{{$t('subscriber.subject')}}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="target in targets" :key="target">
                <td>{{$t(getSubscribeTypeLabel(target.subscribeType))}}</td>
                <td>
                  <esup-subject-infos .subject="target.subject" .config="config" .onSubjectClicked="() => viewSubject(target)"></esup-subject-infos>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      <div v-else>
        <ul class="list-group">
          <li v-for="target in targets" :key="target.id" class="list-group-item">
            <esup-subject-infos .subject="target.subject" .config="config" .onSubjectClicked="() => viewSubject(target)"></esup-subject-infos>
          </li>
        </ul>
      </div>
    </div>
  </div>
  <div class="card-footer">
    <button type="button" class="btn btn-info" @click="back()">
      <span class="fas fa-arrow-left"></span>&nbsp;<span> {{$t("entity.action.back")}}</span>
    </button>
  </div>
</template>
<script>
import { computed, readonly } from 'vue'
import ContentService from '@/services/entities/content/ContentService'
import ClassificationService from '@/services/entities/classification/ClassificationService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import DateUtils from '@/services/util/DateUtils'
import UploadUtils from '@/services/util/UploadUtils'
import SubjectService from '@/services/params/SubjectService'
import store from '@/store/index.js'
import ContentDetailAttachment from '@/views/manager/contents/details/templates/ContentDetailAttachment'
import ContentDetailFlash from '@/views/manager/contents/details/templates/ContentDetailFlash'
import ContentDetailNews from '@/views/manager/contents/details/templates/ContentDetailNews'
import ContentDetailMedia from '@/views/manager/contents/details/templates/ContentDetailMedia'
import ContentDetailRessource from '@/views/manager/contents/details/templates/ContentDetailRessource'
import SubjectDetail from '@/views/entities/subject/SubjectDetail'

export default {
  name: 'ContentDetail',
  components: {
    ContentDetailAttachment,
    ContentDetailFlash,
    ContentDetailNews,
    ContentDetailMedia,
    ContentDetailRessource,
    SubjectDetail
  },
  data () {
    return {
      activeNav: 'content',
      contentData: [],
      item: { type: '', redactor: { writingMode: '' }, lastModifiedBy: { displayName: '' }, validatedBy: { displayName: '' } },
      classifications: null,
      targets: null,
      config: {
        getSubjectInfos: null,
        resolveKey: true,
        userDisplayedAttrs: null
      },
      pubContexts: [],
      linkedFiles: [],
      open: false,
      subjectDetail: null
    }
  },
  provide () {
    return {
      item: readonly(computed(() => this.item)),
      open: readonly(computed(() => this.open)),
      linkedFiles: readonly(computed(() => this.linkedFiles)),
      filterLinkedFiles: this.filterLinkedFiles,
      formatDateTime: this.formatDateTime,
      formatDateDayMonthYear: this.formatDateDayMonthYear,
      getCssFileFromType: this.getCssFileFromType,
      getItemTypeLabel: this.getItemTypeLabel,
      getUrlEnclosure: this.getUrlEnclosure,
      showInfo: this.showInfo
    }
  },
  computed: {
    itemStatusList () {
      return EnumDatasService.getItemStatusList()
    },
    subscribeTypeList () {
      return EnumDatasService.getSubscribeTypeList()
    },
    subjectTypeList () {
      return EnumDatasService.getSubjectTypeList()
    },
    userDisplayedAttrs () {
      return SubjectService.getUserDisplayedAttrs()
    }
  },
  methods: {
    // Chargement des données
    loadDatas () {
      if (this.$route.params.id) {
        return new Promise(resolve => {
          ContentService.get(this.$route.params.id).then(result => {
            var classifs = []
            if (result) {
              result.data.classifications.forEach(element => {
                classifs.push(ClassificationService.get(element.keyId))
              })
            }
            Promise.all(classifs).then(resolvedClassifs => {
              resolve([resolvedClassifs, result])
            }).catch(() => {
              resolve([[], result])
            })
          }).catch(() => {
            resolve(null)
          })
        })
      }
    },
    // Initialisation des variables
    initDatas (response) {
      this.contentData = response
      this.item = this.contentData[1].data.item
      this.classifications = this.contentData[0]
      this.targets = this.contentData[1].data.targets
      this.linkedFiles = this.contentData[1].data.linkedFiles || []

      this.config.userDisplayedAttrs = this.userDisplayedAttrs
      this.config.getSubjectInfos = (keyType, keyId) => {
        return SubjectService.getSubjectInfos(keyType, keyId).then(res => {
          if (res) {
            return res.data
          }
        })
      }

      if (this.classifications && this.classifications.length > 0) {
        this.classifications.forEach(classification => {
          var found = false
          this.pubContexts.forEach(pubContext => {
            if (pubContext.id === classification.data.publisher.id) {
              if (!(classification.data.publisher.context.redactor.writingMode === 'STATIC' &&
                classification.data.publisher.context.reader.authorizedTypes.includes('FLASH'))) {
                pubContext.classifications.push(classification.data)
                found = true
              }
            }
          })
          if (!found) {
            var classifs = []
            if (!(classification.data.publisher.context.redactor.writingMode === 'STATIC' &&
              classification.data.publisher.context.reader.authorizedTypes.includes('FLASH'))) {
              classifs.push(classification.data)
            }
            this.pubContexts.push({ id: classification.data.publisher.id, publisher: classification.data.publisher, classifications: classifs })
          }
        })
      }
    },
    getSubscribeTypeLabel (name) {
      return this.subscribeTypeList.find(val => val.name === name).label
    },
    getSubjectTypeLabel (name) {
      return this.subjectTypeList.find(val => val.name === name).label
    },
    getItemTypeLabel (name) {
      return this.itemStatusList.find(val => val.name === name).label
    },
    // Formatage d'une date au format 'DD MMMM YYYY, HH:mm:ss'
    formatDateTime (date) {
      return DateUtils.convertToIntString(DateUtils.convertDateTimeFromServer(date), {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
      }, store.getters.getLanguage)
    },
    // Formatage d'une date au format 'DD MMMM YYYY'
    formatDateDayMonthYear (date) {
      return DateUtils.convertToIntString(DateUtils.convertDateTimeFromServer(date), {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      }, store.getters.getLanguage)
    },
    // Filtre sur les fichiers
    filterLinkedFiles (linkedFiles) {
      var result = []
      linkedFiles.forEach(element => {
        if (element.inBody === false) {
          result.push(element)
        }
      })
      return result
    },
    // Charge le css en fonction du type de fichier envoyé
    getCssFileFromType (type, fileName) {
      return UploadUtils.getCssFileFromType(type, fileName)
    },
    // Récupération de fichier (local ou distant)
    getUrlEnclosure (enclosure) {
      return enclosure.startsWith('https:') || enclosure.startsWith('http:') || enclosure.startsWith('ftp:') ? enclosure : process.env.VUE_APP_BACK_BASE_URL + enclosure
    },
    // Retour à la page précédente
    back () {
      const previousRoute = store.getters.getPreviousRoute
      if (previousRoute && previousRoute.name) {
        this.$router.push({ name: previousRoute.name, params: previousRoute.params })
      } else {
        this.$router.push({ name: 'Home' })
      }
    },
    // Gestion de la navigation sur la visualisation d'une publication
    showNav (div) {
      this.activeNav = div
    },
    // Ouvre/ferme le détail depuis la page de visualisation d'une publication
    showInfo () {
      this.open = !this.open
    },
    viewSubject (target) {
      if (target.subject.modelId) {
        this.subjectDetail.showSubjectModal(target.subject.modelId)
      }
    }
  },
  mounted () {
    this.subjectDetail = this.$refs.subjectDetail
  },
  created () {
    this.loadDatas().then(res => {
      this.initDatas(res)
    })
  }
}
</script>
