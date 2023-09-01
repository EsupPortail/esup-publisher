<template>
  <div class="row mx-0">
    <div class="col-lg-4">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-lg-8">
      <h1>{{ $t('errors.title') }}</h1>
      <div v-if="errorMessage">
        <div class="alert alert-danger">
          {{ $t(errorMessage) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Error',
  data() {
    return {
      errorMessage: null,
    };
  },
};
</script>
