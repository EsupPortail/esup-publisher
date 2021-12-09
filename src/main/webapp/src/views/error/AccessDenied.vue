<template>
  <div class="row mx-0">
      <div class="col-lg-4">
          <span class="hipster img-fluid rounded"></span>
      </div>
      <div class="col-lg-8">
          <h1>{{ $t("errors.title") }}</h1>
          <div class="alert alert-danger" translate="errors.403" >
            {{ $t("errors.403") }}
          </div>
      </div>
  </div>
</template>
