<template>

  <h2>{{ $t("manager.contents.owned.title") }}</h2>

  <div class="modal fade" id="deleteItemConfirmation" ref="deleteItemConfirmation">
      <div class="modal-dialog">
          <div class="modal-content">
              <form name="deleteForm">
                  <div class="modal-header">
                    <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
                    <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"></button>
                  </div>
                  <div class="modal-body">
                      <p>{{$t('item.delete.question', {id: item.title})}}</p>
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                        <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                    </button>
                    <button type="button" class="btn btn-danger" @click="confirmDelete(item.id)">
                        <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
                    </button>
                  </div>
              </form>
          </div>
      </div>
  </div>

  <div id="tabs">
    <h4>{{$t('manager.contents.owned.order')}}</h4>
    <ul class="nav nav-tabs">
      <li v-for="state in itemStateList" :key="state.label" class="nav-item">
        <a :class="{'active': isActiveState(state.id)}" class="nav-link" :aria-current="isActiveState(state.id)" href=""
          @click.prevent="onClickState(state)">{{$t(state.label)}}</a>
      </li>
    </ul>
    <div class="table-responsive table-responsive-to-cards">
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
            <th v-if="organizations !== null && organizations.length > 1">{{$t('item.organization')}}</th>
            <th class="d-none">{{$t('item.redactor')}}</th>
            <th class="action"></th>
          </tr>
        </thead>
          <tbody>
            <tr v-for="item in items" :key="item.id" :class="{highlight:item.highlight}">
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
              <td v-if="organizations !== null && organizations.length > 1" :data-label="$t('item.organization')">{{item.organization.name}}</td>
              <td class="d-none" :data-label="$t('item.redactor')">{{item.redactor.displayName}}</td>
              <td class="action">
                <button type="button" @click="itemDetail(item)" class="btn btn-info btn-sm me-1">
                  <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
                </button>
                <button type="button" @click="update(item.id)" class="btn btn-primary btn-sm me-1">
                  <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
                </button>
                <button type="button" @click="deleteItem(item.id)" class="btn btn-danger btn-sm">
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
  </div>
</template>

<script>
import ItemService from '@/services/entities/item/ItemService'
import ClassificationService from '@/services/entities/classification/ClassificationService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import store from '@/store/index.js'
import ContentService from '@/services/entities/content/ContentService'
import DateUtils from '@/services/util/DateUtils'
import ParseLinkUtils from '@/services/util/ParseLinkUtils'
import UploadUtils from '@/services/util/UploadUtils'
import { Modal } from 'bootstrap'

export default {
  name: 'ContentsOwned',
  data () {
    return {
      items: [],
      item: {},
      page: 1,
      itemState: null,
      links: {
        first: null,
        prev: null,
        last: null,
        next: null
      },
      deleteModal: null,
      classificationHighlighted: {}
    }
  },
  inject: ['organizations'],
  computed: {
    itemStateList () {
      return EnumDatasService.getItemStatusList()
    }
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets
    loadAll () {
      this.itemState = this.$route.params.itemState ? this.getEnumKey(this.$route.params.itemState) : this.getEnumKey('DRAFT')
      ItemService.query({ page: this.page, per_page: 20, owned: true, item_status: this.itemState }).then((response) => {
        if (response) {
          this.links = ParseLinkUtils.parse(response.headers.get('link'))
          this.items = response.data
        }
      }).catch(error => {
        console.error(error)
      })
    },
    itemDetail (item) {
      this.$router.push({ name: 'ContentDetail', params: { id: item.id } })
    },
    update (itemId) {
      this.$router.push({ name: 'PublishPublisher', params: { id: itemId } })
    },
    loadPage (page) {
      if (this.page !== page) {
        this.page = page
        this.loadAll()
      }
    },
    getEnumKey (name) {
      var result = this.itemStateList.find(val => val.name === name)
      if (result) {
        return result.id
      }
      return this.getEnumKey('DRAFT')
    },
    getEnumName (key) {
      var result = this.itemStateList.find(val => val.id === key)
      if (result) {
        return result.name
      }
      return 'DRAFT'
    },
    onClickState (state) {
      this.itemState = state.id
      this.$router.push({ name: 'ContentsOwned', params: { itemState: this.getEnumName(this.itemState) } })
    },
    isActiveState (stateId) {
      return stateId === this.itemState
    },
    deleteItem (id) {
      ItemService.get(id).then(result => {
        this.item = result.data
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    confirmDelete (id) {
      ContentService.delete(id).then(() => {
        this.deleteModal.hide()
        this.loadAll()
      }).catch(error => {
        console.error(error)
      })
    },
    // Fonction de formatage de date avec heure
    formatDate (date) {
      return DateUtils.formatDateTimeToShortIntString(date, store.getters.getLanguage)
    },
    // Fonction de formatage de date
    formatDateSimple (date) {
      return DateUtils.formatDateToShortIntString(date, store.getters.getLanguage)
    },
    // Récupération de fichier (local ou distant)
    getUrlEnclosure (enclosure) {
      return UploadUtils.getInternalUrl(enclosure)
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deleteItemConfirmation)
    this.loadAll()
  },
  created () {
    ClassificationService.highlighted().then(response => {
      this.classificationHighlighted = response.data
    })
  },
  watch: {
    '$route.params.itemState' () {
      this.loadAll()
    }
  }
}
</script>
