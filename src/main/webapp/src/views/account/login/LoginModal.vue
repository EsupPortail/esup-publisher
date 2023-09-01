<template>
  <div id="loginModalAuthentication" class="modal fade in" :class="{ 'show': active, 'd-block': active }" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ $t('login.modal.title-timeout') }}</h5>
        </div>
        <div class="modal-body">
          <p>{{ $t('login.modal.relog.body') }}</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" v-on:click="$emit('relog')">
            {{ $t('login.modal.relog.action') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginModal',
  // Emission d'un événement pour relancer le processus de connexion
  emits: ['relog'],
  props: {
    showModal: Boolean,
  },
  data() {
    return {
      active: this.showModal,
    };
  },
  mounted() {
    this.$emit('relog', false);
  },
};
</script>
