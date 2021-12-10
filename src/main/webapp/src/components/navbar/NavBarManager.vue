<template>
<div class="container-fluid justify-content-center">
    <div class="navbar-brand">
        <!--<button type="button" class="navbar-toggle other-style h1" data-toggle="collapse" data-target="#navbar-collapse">-->
        <button class="navbar-toggler other-style h1" data-bs-toggle="collapse" data-bs-target="#navbar-collapse">
            <div v-if="isPageNameIncludes('owned') || isPageNameIncludes('managed')">
                <span class="fas fa-file-alt fa-2x"></span>
                <span class="dropdown-toggle">{{ $t("global.menu.manager.contents.main") }}</span>
            </div>
            <div v-else-if="isPageNameIncludes('pending')">
                <span class="fas fa-gavel fa-2x"></span>
                <span class="dropdown-toggle">{{ $t("global.menu.manager.contents.moderate") }}</span>
            </div>
            <div v-else-if="isPageNameIncludes('treeview')">
                <span class="fas fa-gavel fa-2x"></span>
                <span class="dropdown-toggle">{{ $t("global.menu.manager.contexts.main")}}</span>
            </div>
        </button>
    </div>
    <div class="collapse navbar-collapse" id="navbar-collapse">
        <ul class="nav navbar-nav nav-pills justify-content-center">
            <li class="nav-item">
                <router-link to="/home" class="nav-link">
                    <span class="fas fa-home fa-2x"></span>
                    <span>{{$t("global.menu.home")}}</span>
                </router-link>
            </li>
            <li class="nav-item">
                <a class="nav-link" ui-sref="publish.publisher">
                    <span class="fas fa-pencil-alt fa-2x"></span>
                    <span translate="global.menu.write">{{$t("global.menu.write")}}</span>
                </a>
            </li>
            <li v-if="!canModerate" class="nav-item">
                <a class="nav-link">
                    <span class="fas fa-file-alt fa-2x"></span>
                    <span>{{ $t("global.menu.manager.contents.main") }}</span>
                </a>
            </li>
            <li v-else class="nav-item dropdown" :class="{active: isPageNameIncludes('owned') ||  pageName.includes('managed')}" >
                <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown">
                    <span>
                        <span class="fas fa-file-alt fa-2x"></span>
                        <span>{{ $t("global.menu.manager.contents.main") }}</span>
                    </span>
                </a>
                <ul class="dropdown-menu">
                    <li class="dropdown-item" :class="{active: isPageNameIncludes('owned')}"><a ui-sref="owned"><span class="fas fa-asterisk"></span>
                        &#xA0;<span>{{ $t("global.menu.manager.contents.owned") }}</span></a></li>
                    <li class="dropdown-item" :class="{active: isPageNameIncludes('managed')}"><a ui-sref="managed"><span class="fas fa-asterisk"></span>
                        &#xA0;<span>{{ $t("global.menu.manager.contents.managed") }}</span></a></li>
                </ul>
            </li>
            <li v-if="canModerate" :class="{active: isPageNameIncludes('pending')}">
                <a class="nav-link">
                    <span class="fas fa-gavel fa-2x"></span>
                    <span>{{ $t("global.menu.manager.contents.moderate") }}</span>
                </a>
            </li>

            <li :class="{active: isPageNameIncludes('treeview')}" can-moderate>
                <a class="nav-link">
                    <span class="fas fa-wrench fa-2x"></span>
                    <span >{{ $t("global.menu.manager.contexts.main") }}</span>
                </a>
            </li>
        </ul>
    </div>
</div>
</template>

<script>
import UserService from '../../services/user/UserService'
export default {
  name: 'NavBarManager',
  props: ['pageName'],
  components: {},
  data () {
    return {
      canModerate: null
    }
  },
  computed: {
  },
  methods: {
    // Méthode permettant de surligner l'onglet sur
    // lequel l'utilisateur est positionné
    isPageNameIncludes (value) {
      const pageNameUC = this.pageName.toUpperCase()
      const valueUC = value.toUpperCase()
      return pageNameUC.includes(valueUC)
    }
  },
  created () {
    UserService.canModerateAnyThing().then(data => {
      this.canModerate = data.value
    })
  }
}
</script>
