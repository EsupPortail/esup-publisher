<template>
  <h2>{{ $t('manager.contents.managed.title') }}</h2>

  <div class="modal fade" id="deleteItemConfirmation" ref="deleteItemConfirmation">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="deleteForm">
          <div class="modal-header">
            <h4 class="modal-title">{{ $t('entity.delete.title') }}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>{{ $t('item.delete.question', { id: item.title }) }}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
              <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmDelete(item.id)">
              <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="modal fade" id="validateItemConfirmation" ref="invalidateItemConfirmation">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="invalidateForm">
          <div class="modal-header">
            <h4 class="modal-title">{{ $t('entity.unvalidate.title') }}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>{{ $t('item.unvalidate-question', { id: item.title }) }}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
              <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmInvalidate(item.id)">
              <span class="far fa-check-circle"></span>&nbsp;<span>{{ $t('entity.action.unvalidate') }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="modal fade" id="inValidateItemConfirmation" ref="validateItemConfirmation">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="validateForm">
          <div class="modal-header">
            <h4 class="modal-title">{{ $t('entity.validate.title') }}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>{{ $t('item.validate-question', { id: item.title }) }}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
              <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmValidate(item.id)">
              <span class="fas fa-check"></span>&nbsp;<span>{{ $t('entity.action.validate') }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div id="tabs">
    <h4>{{ $t('manager.contents.owned.order') }}</h4>
    <ul class="nav nav-tabs">
      <li v-for="state in itemStateForManager" :key="state.label" class="nav-item">
        <a
          :class="{ active: isActiveState(state.id) }"
          class="nav-link"
          :aria-current="isActiveState(state.id)"
          href=""
          @click.prevent="onClickState(state)"
          >{{ $t(state.label) }}</a
        >
      </li>
    </ul>
    <div class="table-responsive table-responsive-to-cards">
      <div v-if="organizations !== null && organizations.length > 1" class="filter-area row">
        <h5>{{ $t('manager.contents.filter.title') }}</h5>
        <div class="organization col-auto">
          <label class="control-label visually-hidden" for="organization">{{ $t('organization.home.title') }}</label>
          <select
            class="form-select"
            id="organization"
            name="organization"
            v-model="organization_filter"
            @change="loadOrganization(organization_filter)"
          >
            <option value="-1">
              {{ $t('organization.filter.option.all') }}
            </option>
            <option v-for="org in sortedOrganizations" :key="org.id" :value="org.id">
              {{ org.name }}
            </option>
          </select>
        </div>
      </div>
      <table class="table table-striped">
        <thead>
          <tr>
            <th class="d-none">ID</th>
            <th>{{ $t('item.type') }}</th>
            <th>{{ $t('item.title') }}</th>
            <th>{{ $t('item.created') }}</th>
            <th class="d-none">{{ $t('item.enclosure') }}</th>
            <th class="fixed-date-width">{{ $t('item.startDate') }}</th>
            <th class="fixed-date-width">{{ $t('item.endDate') }}</th>
            <th>{{ $t('item.lastModified') }}</th>
            <th class="d-xl-none d-lg-none">{{ $t('item.validated') }}</th>
            <th class="d-none">{{ $t('item.status') }}</th>
            <th class="d-lg-none">{{ $t('item.summary') }}</th>
            <th class="d-none">{{ $t('item.body') }}</th>
            <th class="d-md-none d-lg-none d-xl-table-cell">
              {{ $t('item.rssAllowed') }}
            </th>
            <th v-if="organizations !== null && organizations.length > 1 && organization_filter == -1">
              {{ $t('item.organization') }}
            </th>
            <th class="d-none">{{ $t('item.redactor') }}</th>
            <th class="action"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id" :class="{ highlight: item.highlight }">
            <td class="d-none" data-label="ID">
              <router-link :to="{ name: 'ContentDetail', params: { id: item.id } }">{{ item.id }}</router-link>
            </td>
            <td :data-label="$t('item.type')">
              {{ $t('enum.itemType.' + item.type) }}
            </td>
            <td class="longtext" :data-label="$t('item.title')">
              {{ item.title }}
            </td>
            <td :data-label="$t('item.created')">
              <span :data-label="$t('item.beforeDate')">{{ formatDate(item.createdDate) }}</span>
              <span :data-label="$t('item.beforeName')">{{ item.createdBy.displayName }}</span>
            </td>
            <td class="d-none" :data-label="$t('item.enclosure')">
              <img
                v-if="item.enclosure"
                id="enclosure"
                :src="getUrlEnclosure(item.enclosure)"
                class="img-responsive img-fluid"
                :alt="$t('item.enclosure')"
              />
            </td>
            <td class="text-center fixed-date-width" :data-label="$t('item.startDate')">
              {{ formatDateSimple(item.startDate) }}
            </td>
            <td class="text-center fixed-date-width" :data-label="$t('item.endDate')">
              {{ formatDateSimple(item.endDate) }}
            </td>
            <td :data-label="$t('item.lastModified')">
              <span :data-label="$t('item.beforeDate')">{{ formatDate(item.lastModifiedDate) }}</span>
              <span :data-label="$t('item.beforeName')">{{ item.lastModifiedBy.displayName }}</span>
            </td>
            <td
              v-if="item.validatedDate !== null || item.validatedBy !== null"
              class="d-xl-none d-lg-none"
              :data-label="$t('item.validated')"
            >
              <span v-if="item.validatedDate !== null" :data-label="$t('item.beforeDate')">{{ formatDate(item.validatedDate) }}</span>
              <span v-if="item.validatedBy !== null" :data-label="$t('item.beforeName')">{{ item.validatedBy.displayName }}</span>
            </td>
            <td class="d-none" :data-label="$t('item.status')">
              {{ item.status }}
            </td>
            <td class="d-lg-none verylongtext" :data-label="$t('item.summary')">
              {{ item.summary }}
            </td>
            <td class="d-none" :data-label="$t('item.body')">
              {{ item.body }}
            </td>
            <td class="d-md-none d-lg-none d-xl-table-cell text-center" :data-label="$t('item.rssAllowed')">
              <input type="checkbox" v-model="item.rssAllowed" disabled />
            </td>
            <td
              v-if="organizations !== null && organizations.length > 1 && organization_filter == -1"
              :data-label="$t('item.organization')"
            >
              {{ item.organization.name }}
            </td>
            <td class="d-none" :data-label="$t('item.redactor')">
              {{ item.redactor.displayName }}
            </td>
            <td class="action">
              <button type="button" @click="itemDetail(item)" class="btn btn-info btn-sm me-1">
                <span class="far fa-eye"></span>&nbsp;<span>{{ $t('entity.action.view') }}</span>
              </button>
              <div class="d-inline" v-can-edit="item.contextKey">
                <button type="button" @click="update(item.id)" class="btn btn-primary btn-sm me-1">
                  <span class="fas fa-pencil"></span>&nbsp;<span>{{ $t('entity.action.edit') }}</span>
                </button>
                <button
                  type="button"
                  v-if="item.status == 'PENDING'"
                  @click="validateItem(item.id)"
                  class="btn btn-warning btn-sm me-1 text-white"
                >
                  <span class="fas fa-check"></span>&nbsp;<span>{{ $t('entity.action.validate') }}</span>
                </button>
                <button
                  type="button"
                  v-if="item.status == 'SCHEDULED' || item.status == 'PUBLISHED'"
                  @click="invalidateItem(item.id)"
                  class="btn btn-warning btn-sm me-1 text-white"
                >
                  <span class="fas fa-ban"></span>&nbsp;<span>{{ $t('entity.action.unvalidate') }}</span>
                </button>
              </div>
              <button type="button" @click="deleteItem(item.id)" class="btn btn-danger btn-sm" v-can-delete="item.contextKey">
                <span class="far fa-trash-can"></span>&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <nav>
        <ul class="pagination">
          <li v-if="links.first" @click.prevent="loadPage(links.first)" class="page-item">
            <a class="page-link" href="">&lt;&lt;</a>
          </li>
          <li v-if="links.prev" @click.prevent="loadPage(links.prev)" class="page-item">
            <a class="page-link" href="">&lt;</a>
          </li>
          <li v-if="page > 2" @click.prevent="loadPage(page - 2)" class="page-item">
            <a class="page-link" href="">{{ page - 2 }}</a>
          </li>
          <li v-if="page > 1" @click.prevent="loadPage(page - 1)" class="page-item">
            <a class="page-link" href="">{{ page - 1 }}</a>
          </li>
          <li @click.prevent="loadPage(page)" class="page-item active">
            <a class="page-link" href="">{{ page }}</a>
          </li>
          <li v-if="page < links.last" @click.prevent="loadPage(page + 1)" class="page-item">
            <a class="page-link" href="">{{ page + 1 }}</a>
          </li>
          <li v-if="page < links.last - 1" @click.prevent="loadPage(page + 2)" class="page-item">
            <a class="page-link" href="">{{ page + 2 }}</a>
          </li>
          <li v-if="links.next" @click.prevent="loadPage(links.next)" class="page-item">
            <a class="page-link" href="">&gt;</a>
          </li>
          <li v-if="links.last" @click.prevent="loadPage(links.last)" class="page-item">
            <a class="page-link" href="">&gt;&gt;</a>
          </li>
        </ul>
      </nav>
      <div class="legend">
        <h5>{{ $t('manager.contents.legend.title') }}</h5>
        <span class="highlight">
          {{
            $t('manager.contents.legend.highlight', {
              name: classificationHighlighted.name,
            })
          }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import ItemService from '@/services/entities/item/ItemService';
import ClassificationService from '@/services/entities/classification/ClassificationService';
import EnumDatasService from '@/services/entities/enum/EnumDatasService';
import store from '@/store/index.js';
import ContentService from '@/services/entities/content/ContentService';
import DateUtils from '@/services/util/DateUtils';
import ParseLinkUtils from '@/services/util/ParseLinkUtils';
import UploadUtils from '@/services/util/UploadUtils';
import CommonUtils from '@/services/util/CommonUtils';
import { Modal } from 'bootstrap';

export default {
  name: 'ContentsManaged',
  data() {
    return {
      items: [],
      item: {},
      page: 1,
      organization_filter: -1,
      itemState: null,
      links: {
        first: null,
        prev: null,
        last: null,
        next: null,
      },
      deleteModal: null,
      invalidateModal: null,
      validateModal: null,
      classificationHighlighted: {},
    };
  },
  inject: ['organizations'],
  computed: {
    itemStateForManager() {
      return EnumDatasService.getItemStatusList().filter((item) => {
        return item.name !== 'DRAFT';
      });
    },
    itemStateList() {
      return EnumDatasService.getItemStatusList();
    },
    sortedOrganizations() {
      var sortedOrganizations = Object.assign([], this.organizations);
      sortedOrganizations.sort((org1, org2) => {
        return CommonUtils.compareString(org1.name, org2.name);
      });
      return sortedOrganizations;
    },
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets
    loadAll() {
      this.itemState = this.$route.params.itemState ? this.getEnumKey(this.$route.params.itemState) : this.getEnumKey('PENDING');
      let query_params = {
        page: this.page,
        per_page: 20,
        item_status: this.itemState,
      };
      if (CommonUtils.isDefined(this.organization_filter) && this.organization_filter > -1) {
        query_params.organization_id = this.organization_filter;
      }
      ItemService.query(query_params)
        .then((response) => {
          if (response) {
            this.links = ParseLinkUtils.parse(response.headers.get('link'));
            this.items = response.data;
          }
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    itemDetail(item) {
      this.$router.push({ name: 'ContentDetail', params: { id: item.id } });
    },
    update(itemId) {
      this.$router.push({ name: 'PublishPublisher', params: { id: itemId } });
    },
    loadPage(page) {
      if (this.page !== page) {
        this.page = page;
        this.loadAll();
      }
    },
    loadOrganization(organization_id) {
      if (organization_id) {
        this.organization_filter = organization_id;
      } else {
        this.organization_filter = -1;
      }
      this.loadAll();
    },
    getEnumKey(name) {
      var result = this.itemStateList.find((val) => val.name === name);
      if (result) {
        return result.id;
      }
      return this.getEnumKey('PENDING');
    },
    getEnumName(key) {
      var result = this.itemStateList.find((val) => val.id === key);
      if (result) {
        return result.name;
      }
      return 'PENDING';
    },
    onClickState(state) {
      this.itemState = state.id;
      this.$router.push({
        name: 'ContentsManaged',
        params: { itemState: this.getEnumName(this.itemState) },
      });
    },
    isActiveState(stateId) {
      return stateId === this.itemState;
    },
    deleteItem(id) {
      ItemService.get(id)
        .then((result) => {
          this.item = result.data;
          this.deleteModal.show();
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    confirmDelete(id) {
      ContentService.delete(id)
        .then(() => {
          this.deleteModal.hide();
          this.loadAll();
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    validateItem(id) {
      ItemService.get(id)
        .then((result) => {
          this.item = result.data;
          this.validateModal.show();
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    confirmValidate(id) {
      ItemService.patch({ objectId: id, attribute: 'validate', value: 'true' })
        .then(() => {
          this.validateModal.hide();
          this.loadAll();
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    invalidateItem(id) {
      ItemService.get(id)
        .then((result) => {
          this.item = result.data;
          this.invalidateModal.show();
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    confirmInvalidate(id) {
      ItemService.patch({ objectId: id, attribute: 'validate', value: 'false' })
        .then(() => {
          this.invalidateModal.hide();
          this.loadAll();
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    // Fonction de formatage de date avec heure
    formatDate(date) {
      return DateUtils.formatDateTimeToShortIntString(date, store.getters.getLanguage);
    },
    // Fonction de formatage de date
    formatDateSimple(date) {
      return DateUtils.formatDateToShortIntString(date, store.getters.getLanguage);
    },
    // Récupération de fichier (local ou distant)
    getUrlEnclosure(enclosure) {
      return UploadUtils.getInternalUrl(enclosure);
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteItemConfirmation);
    this.invalidateModal = new Modal(this.$refs.invalidateItemConfirmation);
    this.validateModal = new Modal(this.$refs.validateItemConfirmation);
    this.loadAll();
  },
  created() {
    ClassificationService.highlighted().then((response) => {
      this.classificationHighlighted = response.data;
    });
  },
  watch: {
    '$route.params.itemState'() {
      this.loadAll();
    },
  },
};
</script>
