<template>
  <div>
    <h2><span>{{$t('publisher.detail.title')}}</span> {{$route.params.id}}</h2>
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>{{$t('entity.detail.field')}}</th>
                    <th>{{$t('entity.detail.value')}}</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <span>{{$t('publisher.context.key')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="publisher.context.organization.name +' - '+ publisher.context.reader.displayName +' - '+ publisher.context.redactor.displayName" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.displayName')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="publisher.displayName" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.permissionType')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="$t(getPermissionClassLabel(publisher.permissionType))" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.defaultDisplayOrder')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="$t(getDisplayOrderTypeLabel(publisher.defaultDisplayOrder))" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.used')}}</span>
                    </td>
                    <td>
                        <input type="checkbox" class="form-control-sm" v-model="publisher.used" disabled>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.displayOrder')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="publisher.displayOrder" disabled>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.hasSubPermsManagement')}}</span>
                    </td>
                    <td>
                        <input type="checkbox" class="form-control-sm" v-model="publisher.hasSubPermsManagement" disabled>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.doHighlight')}}</span>
                    </td>
                    <td>
                        <input type="checkbox" class="form-control-sm" v-model="publisher.doHighlight" disabled>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.createdBy')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="publisher.createdBy.displayName" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.createdDate')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="formatDate(publisher.createdDate)" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.lastModifiedBy')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="publisher.lastModifiedBy.displayName" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('publisher.lastModifiedDate')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="formatDate(publisher.lastModifiedDate)" readonly>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <button type="submit"
            @click="publisherPage"
            class="btn btn-info">
        <span class="fas fa-arrow-left"></span>&nbsp;<span> {{$t('entity.action.back')}}</span>
    </button>
</div>
</template>

<script>
import store from '@/store/index.js'
import PublisherService from '@/services/entities/publisher/PublisherService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import DateUtils from '@/services/util/DateUtils'

export default {
  name: 'PublisherDetail',
  data () {
    return {
      publisher: { context: { organization: {}, redactor: {}, reader: {} }, displayName: null, defaultDisplayOrder: null, permissionType: null, used: false, displayOrder: 0, hasSubPermsManagement: false, doHighlight: false, id: null, lastModifiedBy: { displayName: null }, createdBy: { displayName: null } }
    }
  },
  computed: {
    // Liste des types de permission
    permissionClasses () {
      return EnumDatasService.getPermissionClassList()
    },
    // Liste des types de Display Order
    displayOrderTypeList () {
      return EnumDatasService.getDisplayOrderTypeList()
    }
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData () {
      PublisherService.get(this.$route.params.id).then(response => {
        this.publisher = response.data
      }).catch(error => {
        console.error(error)
      })
    },
    // Fonction de formatage de date
    formatDate (date) {
      return DateUtils.convertToIntString(date, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
      }, store.getters.getLanguage)
    },
    // Méthode de redirection sur la page listant les contextes de publications
    publisherPage () {
      this.$router.push({ name: 'AdminEntityPublisher' })
    },
    getPermissionClassLabel (name) {
      return this.getEnumlabel('permissionClass', name)
    },
    getDisplayOrderTypeLabel (name) {
      return this.getEnumlabel('displayOrderType', name)
    },
    getEnumlabel (type, name) {
      var data
      switch (type) {
        case 'permissionClass':
          data = this.permissionClasses.find(val => {
            return val.name === name
          })
          return data ? data.label : ''
        case 'displayOrderType':
          data = this.displayOrderTypeList.find(val => {
            return val.name === name
          })
          return data ? data.label : ''
      }
      return ''
    }
  },
  created () {
    this.initData()
  }
}
</script>
