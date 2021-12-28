<template>
<div class="container-fluid justify-content-end">
  <div class="navbar-brand">
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar-collapse">
      <span class="navbar-toggler-icon"></span>
    </button>
    <a class="navbar-brand"><span >{{ $t("global.title") }}</span> <span class="navbar-version">v{{backVersion}}</span></a>
  </div>
  <div class="collapse navbar-collapse" id="navbar-collapse">
    <ul class=" nav navbar-nav nav-pills justify-content-center">
      <li class="nav-item" >
        <router-link to="/home" class="nav-link">
          <span class="glyphicon glyphicon-home"></span>
          <span>{{ $t("global.menu.home") }}</span>
        </router-link>
      </li>
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" href="">
          <span>
            <span class="glyphicon glyphicon-th-list"></span>
            <span translate="global.menu.entities.main">{{ $t("global.menu.entities.main") }}</span>
          </span>
        </a>
        <ul class="dropdown-menu">
            <li class="dropdown-item" :class="{active: isPageNameIncludes('organization')}"><router-link to="/organization"><span class="fas fa-asterisk"></span>
            &#xA0;<span>{{ $t("global.menu.entities.organization") }}</span></router-link></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('filter')}"><a ui-sref="filter"><span class="fas fa-asterisk"></span>
            &#xA0;<span>{{ $t("global.menu.entities.filter") }}</span></a></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('reader')}"><a ui-sref="reader"><span class="fas fa-asterisk"></span>
            &#xA0;<span>{{ $t("global.menu.entities.reader") }}</span></a></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('redactor')}"><a ui-sref="redactor"><span class="fas fa-asterisk"></span>
            &#xA0;<span>{{ $t("global.menu.entities.redactor") }}</span></a></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('publisher')}"><a ui-sref="publisher"><span class="fas fa-asterisk"></span>
            &#xA0;<span>{{ $t("global.menu.entities.publisher") }}</span></a></li>
        </ul>
      </li>
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" href="">
          <span>
            <span class="glyphicon glyphicon-tower"></span>
            <span class="hidden-tablet" translate="global.menu.admin.main">{{ $t("global.menu.admin.main") }}</span>
          </span>
        </a>
        <ul class="dropdown-menu">
            <li class="dropdown-item" :class="{active: isPageNameIncludes('metrics')}"><a ui-sref="metrics"><span class="fas fa-tachometer-alt"></span>
                &#xA0;<span >{{ $t("global.menu.admin.metrics") }}</span></a></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('health')}"><router-link to="/health"><span class="fas fa-heart"></span>
                &#xA0;<span >{{ $t("global.menu.admin.health") }}</span></router-link></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('configuration')}"><router-link to="/configuration"><span class="fas fa-list-alt"></span>
                &#xA0;<span >{{ $t("global.menu.admin.configuration") }}</span></router-link></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('audits')}"><a ui-sref="audits"><span class="fas fa-bell"></span>
                &#xA0;<span >{{ $t("global.menu.admin.audits") }}</span></a></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('logs')}"><router-link to="/logs"><span class="fas fa-tasks"></span>
                &#xA0;<span >{{ $t("global.menu.admin.logs") }}</span></router-link></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('docs')}"><router-link to="/docs"><span class="fas fa-book"></span>
                &#xA0;<span >{{ $t("global.menu.admin.apidocs") }}</span></router-link></li>
            <li class="dropdown-item" :class="{active: isPageNameIncludes('console')}" v-if="environment === 'development'"><a target="_blank" :href="`${dbConsoleUrl}`"><span class="fas fa-hdd"></span>
                &#xA0;<span translate="global.menu.admin.database">{{ $t("global.menu.admin.database") }}</span></a></li>
        </ul>
      </li>
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" href="">
          <span>
            <span class="glyphicon glyphicon-flag"></span>
            <span class="hidden-tablet">{{ $t("global.menu.language") }}</span>
          </span>
        </a>
        <ul class="dropdown-menu">
          <li v-active-menu="language" class="dropdown-item" v-for="language in languages" :key="language"  @click.prevent="switchLanguage(language)">
            <a href="">{{ $t('language.' + language) }}</a>
          </li>
        </ul>
      </li>
    </ul>
  </div>
</div>
</template>

<script>
export default {
  name: 'NavBarDefault',
  components: {},
  props: ['pageName'],
  data () {
    return {
      backVersion: process.env.BACK_VERSION,
      environment: process.env.NODE_ENV,
      languages: process.env.VUE_APP_I18N_SUPPORTED_LOCALE.split(','),
      dbConsoleUrl: process.env.VUE_APP_DB_CONSOLE_URL
    }
  },
  computed: {
  },
  methods: {
    // Méthode permettant de surligner l'onglet sur
    // laquelle l'utilisateur est positionné
    isPageNameIncludes (value) {
      const pageNameUC = this.pageName.toUpperCase()
      const valueUC = value.toUpperCase()
      return pageNameUC.includes(valueUC)
    },
    switchLanguage (language) {
      if (this.$i18n.locale !== language) {
        this.$i18n.locale = language
        this.$store.commit('setLang', language)
      }
    }
  }
}
</script>
