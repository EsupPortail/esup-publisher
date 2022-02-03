<template>
  <div :class="getCssClass">
    <div class="content" v-if="initData">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import { computed, readonly } from 'vue'
import OrganizationService from '@/services/entities/organization/OrganizationService'
import RedactorService from '@/services/entities/redactor/RedactorService'
import SubjectService from '@/services/params/SubjectService'

export default {
  name: 'Manager',
  data () {
    return {
      organizations: null,
      redactors: null,
      initData: false
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
      OrganizationService.query().then(response => {
        this.organizations = response.data
      })
      RedactorService.query().then(response => {
        this.redactors = response.data
      })
    }
  },
  created () {
    this.init()
    SubjectService.init().then(() => {
      this.initData = true
    })
  }
}
</script>
