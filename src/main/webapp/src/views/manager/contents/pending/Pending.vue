<template>
  <h2>{{$t('manager.contents.pending.title')}}</h2>
  <div class="modal fade" id="deleteItemConfirmation" ref="deleteModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="deleteForm">
          <div class="modal-header">
            <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
              @click="clear"></button>
          </div>
          <div class="modal-body">
            <p>{{$t('item.delete.question', {id: item.title})}}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
              <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmDelete(item)">
              <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="modal fade" id="validateItemConfirmation" ref="validateModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <form name="validateForm">
          <div class="modal-header">
            <h4 class="modal-title">{{$t('entity.validate.title')}}</h4>
            <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
              @click="clear"></button>
          </div>
          <div class="modal-body">
            <p>{{$t('item.validate-question', {id: item.title})}}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
              <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
            </button>
            <button type="button" class="btn btn-danger" @click="confirmValidate(item)">
              <span class="far fa-check-circle"></span>&nbsp;<span>{{$t('entity.action.validate')}}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="table-responsive table-responsive-to-cards">
      <div v-if="organizations !== null && organizations.length > 1" class="filter-area row">
        <h5>{{ $t("manager.contents.filter.title") }}</h5>
        <div class="organization col-auto">
          <label class="control-label visually-hidden" for="organization">{{ $t("organization.home.title") }}</label>
          <select class="form-select" id="organization" name="organization" v-model="organization_filter" @change="loadOrganization(organization_filter)">
            <option value="-1">{{ $t("organization.filter.option.all") }}</option>
            <option v-for="org in sortedOrganizations" :key="org.id" :value="org.id">{{org.name}}</option>
          </select>
        </div>
      </div>
    <table class="table table-striped">
      <thead>
        <tr>
          <th class="d-none">ID</th>
          <th>{{$t('item.type')}}</th>
          <th>{{$t('item.title')}}</th>
          <th>{{$t('item.created')}}</th>
          <th class="d-none">{{$t('item.enclosure')}}</th>
          <th class="fixed-date-width">{{$t('item.startDate')}}</th>
          <th class="fixed-date-width">{{$t('item.endDate')}}</th>
          <th>{{$t('item.lastModified')}}</th>
          <th class="d-xl-none d-lg-none">{{$t('item.validated')}}</th>
          <th class="d-none">{{$t('item.status')}}</th>
          <th class="d-lg-none">{{$t('item.summary')}}</th>
          <th class="d-none">{{$t('item.body')}}</th>
          <th class="d-md-none d-lg-none d-xl-table-cell">{{$t('item.rssAllowed')}}</th>
          <th v-if="organizations !== null && organizations.length > 1 && organization_filter == -1">{{$t('item.organization')}}</th>
          <th class="d-none">{{$t('item.redactor')}}</th>
          <th class="action"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item.id" :class="{highlight: item.highlight}">
          <td class="d-none" data-label="ID"><router-link :to="{ name: 'ContentDetail', params: { id: item.id }}">{{item.id}}</router-link></td>
          <td :data-label="$t('item.type')">{{$t('enum.itemType.' + item.type)}}</td>
          <td class="longtext" :data-label="$t('item.title')">{{item.title}}</td>
          <td :data-label="$t('item.created')">
            <span :data-label="$t('item.beforeDate')">{{formatDate(item.createdDate)}}</span>
            <span :data-label="$t('item.beforeName')">{{item.createdBy.displayName}}</span>
          </td>
          <td class="d-none" :data-label="$t('item.enclosure')">
            <img v-if="item.enclosure" id="enclosure" :src="getUrlEnclosure(item.enclosure)" class="img-responsive img-fluid" :alt="$t('item.enclosure')" />
          </td>
          <td class="text-center fixed-date-width" :data-label="$t('item.startDate')">{{formatDateSimple(item.startDate)}}</td>
          <td class="text-center fixed-date-width" :data-label="$t('item.endDate')">{{formatDateSimple(item.endDate)}}</td>
          <td :data-label="$t('item.lastModified')">
            <span :data-label="$t('item.beforeDate')">{{formatDate(item.lastModifiedDate)}}</span>
            <span :data-label="$t('item.beforeName')">{{item.lastModifiedBy.displayName}}</span>
          </td>
          <td v-if="item.validatedDate !== null || item.validatedBy !== null" class="d-xl-none d-lg-none" :data-label="$t('item.validated')">
              <span v-if="item.validatedDate !== null" :data-label="$t('item.beforeDate')">{{formatDate(item.validatedDate)}}</span>
              <span v-if="item.validatedBy !== null" :data-label="$t('item.beforeName')">{{item.validatedBy.displayName}}</span>
          </td>
          <td class="d-none" :data-label="$t('item.status')">{{item.status}}</td>
          <td class="d-lg-none verylongtext" :data-label="$t('item.summary')">{{item.summary}}</td>
          <td class="d-none" :data-label="$t('item.body')">{{item.body}}</td>
          <td class="d-md-none d-lg-none d-xl-table-cell text-center" :data-label="$t('item.rssAllowed')"><input type="checkbox" v-model="item.rssAllowed" disabled/></td>
          <td v-if="organizations !== null && organizations.length > 1 && organization_filter == -1" :data-label="$t('item.organization')">{{item.organization.name}}</td>
          <td class="d-none" :data-label="$t('item.redactor')">{{item.redactor.displayName}}</td>
          <td class="action">
            <button type="button" class="btn btn-info btn-sm me-1" @click="itemDetail(item.id)">
              <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
            </button>
            <div class="d-inline" v-can-edit="item.contextKey">
              <button type="button" class="btn btn-primary btn-sm me-1" @click="editItem(item.id)">
                <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
              </button>
              <button type="button" class="btn btn-warning btn-sm me-1 text-white" @click="validate(item.id)">
                <span class="far fa-check-circle"></span>&nbsp;<span>{{$t("entity.action.validate")}}</span>
              </button>
            </div>
            <button type="button" class="btn btn-danger btn-sm" @click="deleteItem(item.id)" v-can-delete="item.contextKey">
              <span class="far fa-times-circle"></span>&nbsp;<span>{{$t("entity.action.delete")}}</span>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <nav>
      <ul class="pagination">
        <li v-if="links.first" @click.prevent="loadPage(links.first)" class="page-item"><a class="page-link" href="">&lt;&lt;</a></li>
        <li v-if="links.prev" @click.prevent="loadPage(links.prev)" class="page-item"><a class="page-link" href="">&lt;</a></li>
        <li v-if="page > 2" @click.prevent="loadPage(page - 2)" class="page-item"><a class="page-link" href="">{{page - 2}}</a></li>
        <li v-if="page > 1" @click.prevent="loadPage(page - 1)" class="page-item"><a class="page-link" href="">{{page - 1}}</a></li>
        <li @click.prevent="loadPage(page)" class="page-item active"><a class="page-link" href="">{{page}}</a></li>
        <li v-if="page < links.last" @click.prevent="loadPage(page + 1)" class="page-item"><a class="page-link" href="">{{page + 1}}</a></li>
        <li v-if="page < links.last - 1" @click.prevent="loadPage(page + 2)" class="page-item"><a class="page-link" href="">{{page + 2}}</a></li>
        <li v-if="links.next" @click.prevent="loadPage(links.next)" class="page-item"><a class="page-link" href="">&gt;</a></li>
        <li v-if="links.last" @click.prevent="loadPage(links.last)" class="page-item"><a class="page-link" href="">&gt;&gt;</a></li>
      </ul>
    </nav>
    <div class="legend">
      <h5>{{$t("manager.contents.legend.title")}}</h5>
      <span class="highlight">
        {{$t("manager.contents.legend.highlight", { name: classificationHighlighted.name })}}
      </span>
    </div>
  </div>
</template>
<script>
import ItemService from '@/services/entities/item/ItemService'
import ContentService from '@/services/entities/content/ContentService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import DateUtils from '@/services/util/DateUtils'
import store from '@/store/index.js'
import ParseLinkUtils from '@/services/util/ParseLinkUtils'
import ClassificationService from '@/services/entities/classification/ClassificationService'
import UploadUtils from '@/services/util/UploadUtils'
import CommonUtils from '@/services/util/CommonUtils'
import { Modal } from 'bootstrap'

export default {
  name: 'Pending',
  data () {
    return {
      items: [],
      page: 1,
      organization_filter: -1,
      item: { title: null, enclosure: null, endDate: null, startDate: null, validatedBy: null, validatedDate: null, status: null, summary: null, body: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null, organizations: null, redactor: null },
      deleteModal: null,
      validateModal: null,
      title: null,
      links: {
        first: null,
        prev: null,
        last: null,
        next: null
      },
      classificationHighlighted: {}
    }
  },
  inject: ['organizations'],
  computed: {
    itemStateList () {
      return EnumDatasService.getItemStatusList()
    },
    sortedOrganizations () {
      var sortedOrganizations = Object.assign([], this.organizations)
      sortedOrganizations.sort((org1, org2) => {
        return CommonUtils.compareString(org1.name, org2.name)
      })
      return sortedOrganizations
    }
  },
  methods: {
    loadAll () {
      let query_params = { page: this.page, per_page: 20, owned: false, item_status: this.getEnumKey('PENDING')}
      if (CommonUtils.isDefined(this.organization_filter) && this.organization_filter > -1) {
        query_params.organization_id =  this.organization_filter
      }
      ItemService.query(query_params).then((response) => {
        this.items = response.data
        this.links = ParseLinkUtils.parse(response.headers.get('link'))
      }).catch(error => {
        console.error(error)
      })
    },
    loadPage (page) {
      if (this.page !== page) {
        this.page = page
        this.loadAll()
      }
    },
    loadOrganization (organization_id) {
      if (organization_id) {
        this.organization_filter = organization_id
      } else {
        this.organization_filter = -1
      }
      this.loadAll()
    },
    // Fonction de formatage de date avec heure etc
    formatDate (date) {
      return DateUtils.formatDateTimeToShortIntString(date, store.getters.getLanguage)
    },
    // Fonction de formatage de date
    formatDateSimple (date) {
      return DateUtils.formatDateToShortIntString(date, store.getters.getLanguage)
    },
    validate (id) {
      ItemService.get(id).then(result => {
        this.item = result.data
        this.validateModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression d'une publication
    confirmValidate (item) {
      ItemService.patch({ objectId: item.id, attribute: 'validate', value: 'true' }).then(() => {
        this.validateModal.hide()
        this.loadAll()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression d'une publication
    deleteItem (id) {
      ItemService.get(id).then(response => {
        this.item = response.data
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge de supprimer un item
    confirmDelete (item) {
      ContentService.delete(item.id).then(() => {
        this.deleteModal.hide()
        this.loadAll()
      }).catch(error => {
        console.error(error)
      })
    },
    getEnumKey (name) {
      return this.itemStateList.find(val => val.name === name).id
    },
    itemDetail (itemId) {
      this.$router.push({ name: 'ContentDetail', params: { id: itemId } })
    },
    editItem (itemId) {
      this.$router.push({ name: 'PublishPublisher', params: { id: itemId } })
    },
    // Récupération de fichier (local ou distant)
    getUrlEnclosure (enclosure) {
      return UploadUtils.getInternalUrl(enclosure)
    }
  },
  mounted () {
    this.loadAll()
    this.deleteModal = new Modal(this.$refs.deleteModal)
    this.validateModal = new Modal(this.$refs.validateModal)
  },
  created () {
    ClassificationService.highlighted().then(response => {
      this.classificationHighlighted = response.data
    })
  }
}
</script>
