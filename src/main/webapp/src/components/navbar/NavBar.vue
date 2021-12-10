<template>
  <nav class="navbar navbar-light navbar-expand-md justify-content-center" role="navigation">
    <NavBarManager v-if="isNavBarViewEquals('navBarManager')" :pageName="pageName"></NavBarManager>
    <NavBarPublish v-else-if="isNavBarViewEquals('navBarPublish')"></NavBarPublish>
    <NavBarDefault v-else-if="isNavBarViewEquals('navBarDefault')" :pageName="pageName"></NavBarDefault>
  </nav>
</template>

<script>
import NavBarDefault from '@/components/navbar/NavBarDefault'
import NavBarManager from '@/components/navbar/NavBarManager'
import NavBarPublish from '@/components/navbar/NavBarPublish'
export default {
  name: 'NavBarComponent',
  components: {
    NavBarDefault,
    NavBarManager,
    NavBarPublish
  },
  data () {
    return {
      navBarView: '',
      pageName: ''
    }
  },
  computed: {
  },
  methods: {
    isNavBarViewEquals (value) {
      const navBarViewUC = this.navBarView.toUpperCase()
      const valueUC = value.toUpperCase()
      return navBarViewUC === valueUC
    }
  },
  watch: {
    $route (to, from) {
      this.navBarView = to.meta.navBarView || ''
      this.pageName = to.name
    }
  }
}
</script>
<style lang="scss">
.navbar {
  .dropdown-item {
    padding: 0;
    > a {
      padding: 0.25rem 1rem;
      width: 100%;
      display: inline-block;
    }
  }
}
</style>
