<template>
  <div v-if="showSpinner" class="overlay d-flex align-items-center justify-content-center">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">{{ $t("wait.dialog") }}</span>
    </div>
  </div>
</template>
<script>
import FetchWrapper from '@/services/util/FetchWrapper.js'

export default {
  name: 'Spinner',
  data () {
    return {
      showSpinner: false,
      countPendingRequests: FetchWrapper.countPendingRequests()
    }
  },
  watch: {
    'countPendingRequests' (newVal) {
      // Affichage du spinner si une requête est en cours
      this.showSpinner = newVal > 0
    }
  }
}
</script>
<style lang="scss">
.overlay {
  .spinner-border {
    width: 3rem;
    height: 3rem;
    font-size: xx-large;
    color: #b9b9b9;
  }
}
</style>
