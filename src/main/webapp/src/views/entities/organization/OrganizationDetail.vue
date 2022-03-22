<template>
  <div>
    <h2><span>{{$t('organization.detail.title')}}</span> {{$route.params.id}}</h2>
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
                        <span>{{$t('organization.name')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="organization.name" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.description')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="organization.description" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.displayOrder')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="organization.displayOrder" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.allowNotifications')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="organization.allowNotifications" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.identifiers')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="identifiersFormat" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.createdBy')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="organization.createdBy.displayName" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.createdDate')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="formatDate(organization.createdDate)" readonly> <!-- | date:'dd MMMM yyyy HH:mm:ss' -->
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.lastModifiedBy')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="organization.lastModifiedBy.displayName" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('organization.lastModifiedDate')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="formatDate(organization.lastModifiedDate)" readonly> <!-- | date:'dd MMMM yyyy HH:mm:ss' -->
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <button type="submit"
            @click="organizationPage"
            class="btn btn-info">
        <span class="fas fa-arrow-left"></span>&nbsp;<span> {{$t('entity.action.back')}}</span>
    </button>
</div>
</template>

<script>
import store from '@/store/index.js'
import OrganizationService from '@/services/entities/organization/OrganizationService'
import DateUtils from '@/services/util/DateUtils'

export default {
  name: 'OrganizationDetail',
  data () {
    return {
      organization: { name: null, displayName: null, description: null, lastModifiedBy: { displayName: null }, createdBy: { displayName: null }, displayOrder: null, allowNotifications: false, identifiers: [], id: null }
    }
  },
  computed: {
    identifiersFormat () {
      return this.organization.identifiers.join(', ')
    }
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData () {
      OrganizationService.get(this.$route.params.id).then(response => {
        this.organization = response.data
      }).catch(error => {
        console.error(error)
      })
    },
    // Fonction de formatage de date
    formatDate (date) {
      return DateUtils.formatDateTimeToLongIntString(date, store.getters.getLanguage)
    },
    // Méthode de redirection sur la page listant les structures
    organizationPage () {
      this.$router.push({ name: 'AdminEntityOrganization' })
    }
  },
  created () {
    this.initData()
  }
}
</script>
