<template>
  <div>
    <h2><span>{{$t('filter.detail.title')}}</span> {{$route.params.id}}</h2>
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
                        <span>{{$t('filter.pattern')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="filter.pattern" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('filter.description')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="filter.description" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('filter.type')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="filter.type" readonly>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span>{{$t('filter.organization')}}</span>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" :value="filter.organization.name" readonly>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <button type="submit"
            @click="filterPage"
            class="btn btn-info">
        <span class="fas fa-arrow-left"></span>&nbsp;<span> {{$t('entity.action.back')}}</span>
    </button>
</div>
</template>

<script>
import FilterService from '@/services/entities/filter/FilterService'
export default {
  name: 'FilterDetail',
  data () {
    return {
      filter: { pattern: null, type: null, description: null, organization: { name: null }, id: null }
    }
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData () {
      FilterService.get(this.$route.params.id).then(response => {
        this.filter = response.data
      }).catch(error => {
        // eslint-disable-next-line
        console.error(error)
      })
    },
    // Méthode de redirection sur la page listant les filtres
    filterPage () {
      this.$router.push({ name: 'AdminEntityFilter' })
    }
  },
  created () {
    this.initData()
  }
}
</script>
