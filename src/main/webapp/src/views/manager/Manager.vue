<template>
  <div :class="getCssClass">
    <div class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import { computed, readonly } from 'vue'
import OrganizationService from '@/services/entities/organization/OrganizationService'
import RedactorService from '@/services/entities/redactor/RedactorService'

export default {
  name: 'Manager',
  data () {
    return {
      organizations: null,
      redactors: null
    }
  },
  provide () {
    return {
      organizations: readonly(computed(() => this.organizations)),
      redactors: readonly(computed(() => this.redactors))
    }
  },
  computed: {
    // Détermine le style css de la page
    getCssClass () {
      const classe = [
        'manager',
        this.$router.currentRoute.value.meta.managerCssClass
      ]
      return classe
    }
  },
  methods: {
    init () {
      OrganizationService.query().then(data => {
        this.organizations = data
      })
      RedactorService.query().then(data => {
        this.redactors = data
      })
    }
  },
  created () {
    this.init()
  }
}
</script>
