<template>
  <h3 class="mt-3 mb-2">
    {{ $t('permissionOnClassificationWithSubjectList.detail.title') }} &nbsp;
    <span
      class="hand"
      @click="changePermissionAdvanced()"
      v-has-role="'ROLE_ADMIN'"
      v-tooltip="$t('permission.advancedMode')"
      :class="{ disabled: !permissionAdvanced }"
      ><i class="fa fa-graduation-cap"
    /></span>
  </h3>

  <SubjectDetail ref="subjectDetail"></SubjectDetail>

  <div v-show="!addPerm" aria-labelledby="addPermissionOnContextWithSubjectListLabel" aria-hidden="true">
    <form name="editPermOnCtxForm" role="form">
      <div class="header">
        <h4 id="myPermissionOnContextLabel">
          {{ $t('permissionOnClassificationWithSubjectList.home.createOrEditLabel') }}
        </h4>
      </div>
      <div class="body">
        <div class="form-group">
          <label for="permRole">{{ $t('permissionOnClassificationWithSubjectList.role') }}</label>
          <select class="form-select" id="permRole" name="permRole" v-model="permission.role">
            <option v-for="role in availableRoles" :key="role.id" :value="role.name">
              {{ $t(role.label) }}
            </option>
          </select>
        </div>

        <div class="form-group" v-show="permissionAdvanced">
          <label>{{ $t('permissionOnClassificationWithSubjectList.evaluator') }}</label>
          <esup-edit-evaluator
            ref="editEvaluatorPermissionOnCtx"
            .evaluator="permission.evaluator"
            .config="editEvaluatorConfig"
            .onModification="onModificationEvaluator"
            .onSubjectClicked="(subject) => viewSubject(subject)"
          ></esup-edit-evaluator>
          <div class="invalid-feedback d-block" v-if="editEvaluatorError">
            {{ $t('entity.validation.required') }}
          </div>
        </div>

        <div class="form-group" v-show="!permissionAdvanced && !isAdvancedEvaluator(permission.evaluator)">
          <label class="control-label">{{ $t('permissionOnClassificationWithSubjectList.evaluatorsimple') }}</label>
          <esup-subject-search-button
            .searchId="'permSubject'"
            .config="subjectSearchButtonConfig"
            .onSelection="updateSelectedSubject"
          ></esup-subject-search-button>
          <ul class="list-group list-unstyled">
            <li v-for="subject in selectedSubjects" :key="subject.keyId" class="list-group-item">
              <esup-subject-infos .subject="subject" .config="subjectInfosConfig" .onSubjectClicked="() => viewSubject(subject)">
                <a @click.prevent="removeFromSelectedSubjects(subject)" v-tooltip="$t('manager.publish.targets.remove')" href=""
                  ><i class="far fa-trash-can text-danger"></i></a
                >&nbsp;
              </esup-subject-infos>
            </li>
          </ul>
          <div class="invalid-feedback d-block" v-if="!areSubjectsSelected()">
            {{ $t('entity.validation.required') }}
          </div>
        </div>

        <div class="form-group" v-show="!permissionAdvanced && isAdvancedEvaluator(permission.evaluator)">
          <span>{{ $t('evaluators.forAdvancedOnly') }}</span>
        </div>
      </div>

      <div class="form-group">
        <label>{{ $t('permissionOnClassificationWithSubjectList.authorizedSubjects') }}</label>
        <esup-subject-search-button
          .searchId="'authorizedSubject'"
          .config="subjectSearchButtonConfig"
          .onSelection="updateAuthorizedSubjects"
        ></esup-subject-search-button>
        <ul class="list-group list-unstyled">
          <li v-for="target in permission.authorizedSubjects" :key="target.keyId" class="list-group-item">
            <esup-subject-infos .subject="target" .config="subjectInfosConfig" .onSubjectClicked="() => viewSubject(subject)">
              <a @click.prevent="removePermTarget(target)" v-tooltip="$t('manager.publish.targets.remove')" href=""
                ><i class="far fa-trash-can text-danger"></i></a
              >&nbsp;
            </esup-subject-infos>
          </li>
        </ul>
        <div class="invalid-feedback d-block" v-if="!permission.authorizedSubjects || permission.authorizedSubjects.length < 1">
          {{ $t('entity.validation.required') }}
        </div>
      </div>

      <div class="footer">
        <button type="button" class="btn btn-default btn-outline-dark me-1" @click="clearPermission()">
          <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
        </button>
        <button
          type="button"
          @click="createPermission()"
          :disabled="
            !permission.role ||
            (permissionAdvanced && editEvaluatorError) ||
            (!permissionAdvanced && (!areSubjectsSelected || isAdvancedEvaluator(permission.evaluator))) ||
            !permission.authorizedSubjects ||
            permission.authorizedSubjects.length < 1
          "
          class="btn btn-primary"
        >
          <span class="fas fa-floppy-disk"></span>&nbsp;<span>{{ $t('entity.action.save') }}</span>
        </button>
      </div>
    </form>
  </div>

  <div v-show="addPerm">
    <button type="button" v-if="availableRoles.length > 0 && canEditCtxPerms" class="btn btn-primary" @click="addPermission()">
      <span class="fa fa-bolt"></span>
      <span>{{ $t('permission.home.createLabel') }}</span>
    </button>
    <div class="table-responsive table-responsive-to-cards overflow-visible evaluators" v-show="permissionAdvanced">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t('permission.role') }}</th>
            <th>{{ $t('permission.evaluator') }}</th>
            <th>
              {{ $t('permissionOnClassificationWithSubjectList.authorizedSubjects') }}
            </th>
            <th class="action"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="permission in permissions" :key="permission.id">
            <td :data-label="$t('permission.role')">
              {{ $t(getPermissionTypeLabel(permission.role)) }}
            </td>
            <td class="verylongtext" :data-label="$t('permission.evaluator')">
              <esup-evaluator
                .evaluator="permission.evaluator"
                .config="evaluatorConfig"
                .onSubjectClicked="(subject) => viewSubject(subject)"
              ></esup-evaluator>
            </td>
            <td class="verylongtext" :data-label="$t('permissionOnClassificationWithSubjectList.authorizedSubjects')">
              <ul class="list-unstyled">
                <li v-for="subject in permission.authorizedSubjects" :key="subject" class="list-unstyled">
                  <esup-subject-infos .subject="subject" .config="subjectInfosConfig" .onSubjectClicked="() => viewSubject(subject)">
                  </esup-subject-infos>
                </li>
              </ul>
            </td>
            <td class="action">
              <button type="button" @click="updatePermission(permission.id)" v-if="canEditCtxPerms" class="btn btn-primary me-1">
                <span class="fas fa-pencil"></span>&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button type="button" @click="deletePermission(permission.id)" v-if="canEditCtxPerms" class="btn btn-danger">
                <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="table-responsive table-responsive-to-cards overflow-visible evaluators" v-show="!permissionAdvanced">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t('permission.role') }}</th>
            <th>{{ $t('permissionOnContext.evaluatorsimple') }}</th>
            <th>
              {{ $t('permissionOnClassificationWithSubjectList.authorizedSubjects') }}
            </th>
            <th class="action"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="permission in permissions" :key="permission">
            <td :data-label="$t('permission.role')">
              {{ $t(getPermissionTypeLabel(permission.role)) }}
            </td>
            <td class="verylongtext" :data-label="$t('permissionOnContext.evaluatorsimple')">
              <esup-evaluator
                .evaluator="permission.evaluator"
                .simple="true"
                .config="evaluatorConfig"
                .onSubjectClicked="(subject) => viewSubject(subject)"
              ></esup-evaluator>
            </td>
            <td class="verylongtext" :data-label="$t('permissionOnClassificationWithSubjectList.authorizedSubjects')">
              <ul class="list-unstyled">
                <li v-for="subject in permission.authorizedSubjects" :key="subject" class="list-unstyled">
                  <esup-subject-infos .subject="subject" .config="subjectInfosConfig" .onSubjectClicked="() => viewSubject(subject)">
                  </esup-subject-infos>
                </li>
              </ul>
            </td>
            <td class="action">
              <button
                type="button"
                v-if="!isAdvancedEvaluator(permission.evaluator) && canEditCtxPerms"
                @click="updatePermission(permission.id)"
                class="btn btn-primary me-1"
              >
                <span class="fas fa-pencil"></span>&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button
                type="button"
                v-if="!isAdvancedEvaluator(permission.evaluator) && canEditCtxPerms"
                @click="deletePermission(permission.id)"
                class="btn btn-danger"
              >
                <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div class="modal fade" id="deletePermissionConfirmation" ref="deletePermissionConfirmation">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="deleteForm">
          <div class="modal-header">
            <h4 class="modal-title">{{ $t('entity.delete.title') }}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clearPermission"></button>
          </div>
          <div class="modal-body">
            <p>
              {{
                $t('permission.delete.question', {
                  id: $t(getPermissionTypeLabel(permission.role)),
                })
              }}
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clearPermission">
              <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmDeletePermission(permission.id)">
              <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script>
import GroupService from '@/services/entities/group/GroupService';
import SubjectService from '@/services/params/SubjectService';
import UserService from '@/services/user/UserService';
import SubjectDetail from '@/views/entities/subject/SubjectDetail';

export default {
  name: 'PermissionOnCtxWithSubjects',
  components: {
    SubjectDetail,
  },
  data() {
    return {
      canEditCtxPerms: false,
      subjectDetail: null,
      treeData: [],
      userFunctionnalAttributes: [],
      editEvaluatorConfig: {
        getSubjectInfos: null,
        userDisplayedAttrs: [],
        lang: this.$store.getters.getLanguage,
        operators: [],
        stringEvaluators: [],
        userAttributes: [],
        treeGroupDatas: [],
        searchUsers: null,
        getGroupMembers: null,
      },
      evaluatorConfig: {
        getSubjectInfos: null,
        userDisplayedAttrs: [],
        lang: this.$store.getters.getLanguage,
      },
      subjectSearchButtonConfig: {
        treeGroupDatas: [],
        userDisplayedAttrs: [],
        extendedAttrs: [],
        searchUsers: null,
        getGroupMembers: null,
        lang: this.$store.getters.getLanguage,
      },
    };
  },
  inject: [
    'addPerm',
    'selectedSubjects',
    'permissions',
    'permission',
    'permissionAdvanced',
    'availableRoles',
    'isDatasLoad',
    'editEvaluatorError',
    'setIsDatasLoad',
    'context',
    'userDisplayedAttrs',
    'userFonctionalAttrs',
    'deletePermission',
    'changePermissionAdvanced',
    'updateSelectedSubject',
    'removeFromSelectedSubjects',
    'areSubjectsSelected',
    'clearPermission',
    'createPermission',
    'addPermission',
    'getPermissionTypeLabel',
    'updatePermission',
    'onModificationEditEvaluator',
    'isAdvancedEvaluator',
    'subjectInfosConfig',
    'selectPermissionManager',
    'operatorTypeList',
    'stringEvaluationModeList',
    'updateAuthorizedSubjects',
    'removePermTarget',
  ],
  methods: {
    initEvaluator() {
      this.evaluatorConfig.getSubjectInfos = this.subjectInfosConfig.getSubjectInfos;
      this.evaluatorConfig.userDisplayedAttrs = this.userDisplayedAttrs;
    },
    // Chargement du niveau 0 du treeview pour le webcomponent esup-subject-search-button
    loadTreeDataSearchButton() {
      GroupService.search({
        context: this.context.contextKey,
        search: 1,
        subContexts: [],
      }).then((response) => {
        this.treeData = response.data;
        this.treeData.forEach((element) => this.initTreeNodeProperties(element));
        // Initialisation de la configuration du webcomponent esup-subject-search-button
        this.subjectSearchButtonConfig.treeGroupDatas = this.treeData;
        this.subjectSearchButtonConfig.userDisplayedAttrs = this.userDisplayedAttrs;
        this.subjectSearchButtonConfig.extendedAttrs = this.userFonctionalAttrs;
        this.subjectSearchButtonConfig.getGroupMembers = (id) =>
          GroupService.userMembers(id).then((res) => {
            return res.data;
          });
        this.subjectSearchButtonConfig.searchUsers = (search) =>
          UserService.search({
            context: this.context.contextKey,
            search: search,
            subContexts: [],
          }).then((res) => {
            return res.data;
          });
        // Initialisation de la configuration du webcomponent esup-edit-evaluator
        this.editEvaluatorConfig.treeGroupDatas = this.treeData;
        this.editEvaluatorConfig.userDisplayedAttrs = this.userDisplayedAttrs;
        this.editEvaluatorConfig.userAttributes = this.userFunctionnalAttributes;
        this.editEvaluatorConfig.operators = this.operatorTypeList;
        this.editEvaluatorConfig.stringEvaluators = this.stringEvaluationModeList;
        this.editEvaluatorConfig.getGroupMembers = (id) =>
          GroupService.userMembers(id).then((res) => {
            return res.data;
          });
        this.editEvaluatorConfig.searchUsers = (search) =>
          UserService.search({
            context: this.context.contextKey,
            search: search,
            subContexts: [],
          }).then((res) => {
            return res.data;
          });
        this.editEvaluatorConfig.getSubjectInfos = (keyType, keyId) => {
          return SubjectService.getSubjectInfos(keyType, keyId).then((res) => {
            if (res) {
              return res.data;
            }
          });
        };
        this.setIsDatasLoad(true);
      });
    },
    // Mise à jour asynchrone des enfants du treeview
    loadTreeDataChildrenSearchButton(id) {
      return GroupService.search({
        context: this.context.contextKey,
        search: id,
        subContexts: [],
      }).then((response) => {
        response.data.forEach((element) => this.initTreeNodeProperties(element));
        return response.data;
      });
    },
    // Intialisation des propriétés d'un noeud de l'arbre
    initTreeNodeProperties(node) {
      if (node.children) {
        node.getChildren = () => this.loadTreeDataChildrenSearchButton(node.id);
      }
    },
    // Affichage des informations de la cible
    viewSubject(subject) {
      if (subject) {
        if (subject.subjectDTO && subject.subjectDTO.modelId) {
          this.subjectDetail.showSubjectModal(subject.subjectDTO.modelId);
        } else if (subject.modelId) {
          this.subjectDetail.showSubjectModal(subject.modelId);
        } else if (subject.keyId && subject.keyType) {
          this.subjectDetail.showSubjectModal(subject);
        }
      }
    },
    onModificationEvaluator(data) {
      this.onModificationEditEvaluator(data, this.$refs.editEvaluatorPermissionOnCtx.isValid());
    },
  },
  mounted() {
    this.subjectDetail = this.$refs.subjectDetail;
  },
  created() {
    UserService.canEditCtxPerms(this.context.contextKey.keyId, this.context.contextKey.keyType).then((response) => {
      this.canEditCtxPerms = response.data.value;
    });
    UserService.funtionalAttributes().then((response) => {
      this.userFunctionnalAttributes = response.data;
    });
    this.initEvaluator();
    this.loadTreeDataSearchButton();
  },
};
</script>
