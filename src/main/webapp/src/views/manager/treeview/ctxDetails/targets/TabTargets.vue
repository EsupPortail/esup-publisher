<template>
  <div>
    <h3 class="mt-3 mb-2" v-if="context.contextKey.keyType !== 'ORGANIZATION' && context.contextKey.keyType !== 'PUBLISHER'">{{$t('manager.treeview.details.targets')}}</h3>
    <h3 class="mt-3 mb-2" v-if="context.contextKey.keyType === 'ORGANIZATION' || context.contextKey.keyType === 'PUBLISHER'">{{$t('manager.treeview.details.defaultTargets')}}</h3>

    <SubjectDetail ref="subjectDetail"></SubjectDetail>

    <div v-show="addSubs" aria-labelledby="addSubscriberOnContext" aria-hidden="true">
      <form name="editSubscriberForm" role="form" novalidate>
        <div class="header">
          <h4 id="mySubscriberLabel">{{$t('subscriber.home.createOrEditLabel')}}</h4>
        </div>
        <div class="body">
          <div class="form-group" v-if="showSubscribeType()">
            <label class="control-label" for="subscribeType">{{$t('subscriber.subscribeType')}}</label>
            <select class="form-select" id="subscribeType" name="subscribeType" v-model="subscriber.subscribeType">
              <option v-for="subscribeType in subscribeTypeList" :key="subscribeType.id" :value="subscribeType.name">
                {{$t(subscribeType.label)}}
              </option>
            </select>
            <div class="invalid-feedback d-block"
              v-if="!subscriber.subscribeType || subscriber.subscribeType === {}">
              {{ $t('entity.validation.required') }}
            </div>
          </div>
          <div class="form-group">
            <label class="control-label">{{$t('manager.publish.targets.search')}}</label>
            <esup-subject-search-button v-if="!isSubjectSelected() && isDatasLoad" .searchId="'targetSubject'" .withExtended="true"
              .config="subjectSearchButtonConfig" .onSelection="updateSelectedSubject"></esup-subject-search-button>
            <ul v-if="isSubjectSelected()" class="list-group list-unstyled">
              <li class="list-group-item">
                <esup-subject-infos .subject="selectedSubject" .config="subjectInfosConfig" .onSubjectClicked="() => targetDetail(selectedSubject)">
                  <a @click.prevent="removeSelectedSubject()" v-tooltip="$t('manager.publish.targets.remove')" href=""><i class="far fa-times-circle text-danger" ></i></a>&nbsp;
                </esup-subject-infos>
              </li>
            </ul>
            <div class="invalid-feedback d-block"
              v-if="!isSubjectSelected()">
              {{ $t('entity.validation.required') }}
            </div>
          </div>
        </div>
        <div class="footer">
          <button type="button" class="btn btn-default btn-outline-dark me-1" @click="clearSubscriber">
            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
          </button>
          <button type="button" :disabled="!subscriber.subscribeType || subscriber.subscribeType === {} || !isSubjectSelected()" class="btn btn-primary" @click="createSubscriber()">
            <span class="fas fa-download"></span>&nbsp;<span>{{$t('entity.action.save')}}</span>
          </button>
        </div>
      </form>
    </div>

    <div v-show="!addSubs">
      <button class="btn btn-primary" @click="addSubscriber()" v-if="canEditTargetCtx">
        <span class="fas fa-bolt"></span> <span>{{$t('subscriber.home.createLabel')}}</span>
      </button>

      <div class="modal fade" id="deleteTargetConfirmation" ref="deleteTargetConfirmation">
        <div class="modal-dialog">
          <div class="modal-content">
            <form name="deleteForm">
              <div class="modal-header">
                <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
                <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body">
                <p>{{$t('subscriber.delete.question')}}</p>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clearSubscriber">
                  <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                </button>
                <button type="button" class="btn btn-danger" @click="confirmDeleteSubscriber(subscriber)">
                  <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div class="table-responsive" :class="{ 'table-responsive-to-cards': showSubscribeType() }">
        <table class="table table-striped">
          <thead>
            <tr>
              <th v-if="showSubscribeType()">{{$t('subscriber.subscribeType')}}</th>
              <th rowspan="2">{{$t('subscriber.subject')}}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="target in targets" :key="getTargetId(target)">
              <td :data-label="$t('subscriber.subscribeType')" v-if="showSubscribeType()">{{$t(getSubscribeTypeLabel(target.subscribeType))}}</td>
              <td :data-label="$t('subscriber.subject')" v-if="target.subjectDTO">
                <esup-subject-infos .subject="target.subjectDTO" .config="subjectInfosConfig" .onSubjectClicked="() => targetDetail(target)"></esup-subject-infos>
              </td>
              <td :data-label="$t('subscriber.subject')" v-if="!target.subjectDTO">
                <esup-subject-infos .subject="target.subjectKeyExtendedDTO" .config="subjectInfosConfig"></esup-subject-infos>
              </td>
              <td class="action">
                <button type="submit" v-if="canEditTargetCtx" @click="deleteSubscriber(target)" class="btn btn-danger">
                  <span class="far fa-times-circle"></span>&nbsp;<span>{{$t("entity.action.delete")}}</span>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { Modal } from 'bootstrap'
import GroupService from '@/services/entities/group/GroupService'
import SubjectService from '@/services/params/SubjectService'
import SubscriberService from '@/services/entities/subscriber/SubscriberService'
import Base64Utils from '@/services/util/Base64Utils'
import UserService from '@/services/user/UserService'
import SubjectDetail from '@/views/entities/subject/SubjectDetail'
import CommonUtils from '@/services/util/CommonUtils'
import i18n from '@/i18n'

const { t } = i18n.global

export default {
  name: 'TabTargets',
  components: {
    SubjectDetail
  },
  data () {
    return {
      addSubs: false,
      subjectSearchButtonConfig: {
        treeGroupDatas: [],
        userDisplayedAttrs: [],
        extendedAttrs: [],
        searchUsers: null,
        getGroupMembers: null,
        lang: this.$store.getters.getLanguage
      },
      deleteTargetConfirmation: null,
      canEditTargetCtx: false,
      treeData: [],
      isDatasLoad: false,
      selectedSubject: {},
      subscriber: {},
      subscribeTypeList: [],
      subjectCtx: {
        subject_id: null,
        subject_type: null,
        subject_attribute: null,
        ctx_id: null,
        ctx_type: null
      }
    }
  },
  inject: [
    'context', 'targets', 'setTargets', 'ctxType', 'getEnumlabel',
    'getHasTargetManagment', 'getSubjectTypeList', 'load', 'subjectInfosConfig'
  ],
  computed: {
    userDisplayedAttrs () {
      return SubjectService.getUserDisplayedAttrs()
    },
    userFonctionalAttrs () {
      return SubjectService.getUserFonctionalAttrs()
    }
  },
  methods: {
    initDatas () {
      this.subscribeTypeList = [{ name: 'FORCED', id: 0, label: 'enum.subscribe.forced.title' }]
      this.loadTreeDataSearchButton()
    },
    // Chargement du niveau 0 du treeview pour le webcomponent esup-subject-search-button
    loadTreeDataSearchButton () {
      GroupService.search({ context: this.context.contextKey, search: 1, subContexts: [] }).then(response => {
        this.treeData = response.data
        this.treeData.forEach(element => this.initTreeNodeProperties(element))
        this.subjectSearchButtonConfig.treeGroupDatas = this.treeData
        this.subjectSearchButtonConfig.userDisplayedAttrs = this.userDisplayedAttrs
        this.subjectSearchButtonConfig.extendedAttrs = this.userFonctionalAttrs
        this.subjectSearchButtonConfig.getGroupMembers = (id) => GroupService.userMembers(id).then(res => { return res.data })
        this.subjectSearchButtonConfig.searchUsers = (search) => UserService.search({ context: this.context.contextKey, search: search, subContexts: [] }).then(res => { return res.data })

        this.initEditTarget()
      })
    },
    // Mise à jour asynchrone des enfants du treeview
    loadTreeDataChildrenSearchButton (id) {
      return GroupService.search({ context: this.context.contextKey, search: id, subContexts: [] }).then(response => {
        response.data.forEach(element => this.initTreeNodeProperties(element))
        return response.data
      })
    },
    // Intialisation des propriétés d'un noeud de l'arbre
    initTreeNodeProperties (node) {
      if (node.children) {
        node.getChildren = () => this.loadTreeDataChildrenSearchButton(node.id)
      }
    },
    initEditTarget () {
      UserService.canEditCtxTargets(this.context.contextKey.keyId, this.context.contextKey.keyType).then(response => {
        this.canEditTargetCtx = response.data.value
        this.isDatasLoad = true
      })
    },
    // Ajout de la cible sélectionnée
    addSubscriber () {
      this.addSubs = true
      this.selectedSubject = {}
      this.subscriber = { subscribeType: this.subscribeTypeList[0].name }
    },
    // Nettoyage des objets en cas d'abandon de la saisie d'une cible
    clearSubscriber () {
      this.subscriber.subscribeType = this.subscribeTypeList[0].name
      this.selectedSubject = {}
      this.addSubs = false
    },
    // Chargement des données d'une cible en vu de sa suppression
    deleteSubscriber (target) {
      if (target.subjectDTO && target.subjectDTO !== {}) {
        this.subjectCtx.subject_id = Base64Utils.encode(target.subjectDTO.modelId.keyId)
        this.subjectCtx.subject_type = this.getSubjectTypeList(target.subjectDTO.modelId.keyType)
        this.subjectCtx.subject_attribute = 'ID'
        this.subjectCtx.ctx_id = target.contextKeyDTO.keyId
        this.subjectCtx.ctx_type = target.contextKeyDTO.keyType
      } else {
        this.subjectCtx.subject_id = Base64Utils.encode(target.subjectKeyExtendedDTO.keyValue)
        this.subjectCtx.subject_type = this.getSubjectTypeList(target.subjectKeyExtendedDTO.keyType)
        this.subjectCtx.subject_attribute = target.subjectKeyExtendedDTO.keyAttribute
        this.subjectCtx.ctx_id = target.contextKeyDTO.keyId
        this.subjectCtx.ctx_type = target.contextKeyDTO.keyType
      }
      SubscriberService.get(this.subjectCtx).then(result => {
        this.subscriber = result.data
        this.deleteTargetConfirmation.show()
      })
    },
    // Suppression de la cible
    confirmDeleteSubscriber (target) {
      this.subjectCtx.subject_id = Base64Utils.encode(target.subjectCtxId.subject.keyValue)
      this.subjectCtx.subject_type = this.getSubjectTypeList(target.subjectCtxId.subject.keyType)
      this.subjectCtx.subject_attribute = target.subjectCtxId.subject.keyAttribute
      this.subjectCtx.ctx_id = target.subjectCtxId.context.keyId
      this.subjectCtx.ctx_type = target.subjectCtxId.context.keyType
      SubscriberService.delete(this.subjectCtx).then(() => {
        this.subscriber = {}
        this.deleteTargetConfirmation.hide()
        this.load(this.ctxType, this.context.id)
      })
    },
    // Mise à jour du sujet séléctionné
    updateSelectedSubject (datas) {
      var newVal = datas[0]
      if (CommonUtils.isDefined(newVal) && !CommonUtils.equals({}, newVal) && !CommonUtils.equals(newVal, this.selectedSubject)) {
        var found = false
        for (var i = 0; i < this.targets.length; i++) {
          if (this.targets[i].subjectDTO && CommonUtils.equals(newVal.modelId, this.targets[i].subjectDTO.modelId)) {
            found = true
            break
          }
        }
        if (!found) {
          this.selectedSubject = newVal
        } else {
          this.$toast.warning(t('subscriber.alreadySelected'))
        }
      }
    },
    getSubscribeTypeLabel (name) {
      return this.getEnumlabel('subscribeType', name)
    },
    getTargetId (target) {
      return target.subjectDTO !== null && target.subjectDTO.modelId ? target.subjectDTO.modelId.keyId : target.subjectKeyExtendedDTO.keyValue
    },
    isSubjectSelected () {
      return !CommonUtils.equals(this.selectedSubject, {})
    },
    showSubscribeType () {
      switch (this.$route.params.ctxType) {
        case 'ORGANIZATION':
          return false
        case 'PUBLISHER':
          return false
        default:
          return this.getHasTargetManagment(this.$route.params.ctxType)
      }
    },
    // Affichage des informations de la cible
    targetDetail (target) {
      if (target) {
        if (target.subjectDTO && target.subjectDTO.modelId) {
          this.subjectDetail.showSubjectModal(target.subjectDTO.modelId)
        } else if (target.modelId) {
          this.subjectDetail.showSubjectModal(target.modelId)
        } else if (target.keyId && target.keyType) {
          this.subjectDetail.showSubjectModal(target)
        }
      }
    },
    // Création d'un abonné suite à sa saisie via le webcomponent esup-subject-search-button
    createSubscriber () {
      var subject = (this.selectedSubject.modelId && this.selectedSubject && !CommonUtils.equals(this.selectedSubject.modelId))
        ? { keyValue: this.selectedSubject.modelId.keyId, keyAttribute: 'ID', keyType: this.selectedSubject.modelId.keyType } : this.selectedSubject
      var subscriber = {
        subscribeType: this.subscriber.subscribeType,
        subjectCtxId: {
          subject: subject,
          context: this.context.contextKey
        }
      }
      SubscriberService.save(subscriber).then(() => {
        this.clearSubscriber()
        this.load(this.ctxType, this.context.id)
      })
    },
    // Suppression de la cible suite à sa sélection dans via le webcomponent esup-subject-search-button
    removeSelectedSubject () {
      this.selectedSubject = {}
    }
  },
  mounted () {
    this.subjectDetail = this.$refs.subjectDetail
    this.deleteTargetConfirmation = new Modal(this.$refs.deleteTargetConfirmation)
  },
  created () {
    this.initDatas()
  }
}
</script>
