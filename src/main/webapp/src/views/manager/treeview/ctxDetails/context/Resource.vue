<template>
  <h3 class="mt-3 mb-2">
    {{ $t("manager.treeview.details.context.properties") }}
  </h3>
  <div class="mb-3">
    <button
      type="button"
      @click="detailSubject(context.id)"
      class="btn btn-info btn-sm"
    >
      <span class="far fa-eye"></span>&nbsp;<span>{{
        $t("entity.action.view")
      }}</span>
    </button>
  </div>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.title") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ context.title }}</span>
    </dd>
  </dl>
  <dl
    class="row entity-details"
    v-if="
      context.enclosure &&
      context.enclosure !== '' &&
      context.enclosure !== 'http://'
    "
  >
    <dt class="col-sm-5">
      <span>{{ $t("resource.enclosure") }}</span>
    </dt>
    <dd class="col-sm-7">
      <img
        class="resource-object img-fluid"
        :src="getUrlEnclosure(context.enclosure)"
        alt="image"
      />
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.status") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ $t(getItemStatusLabel(context.status)) }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.summary") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ context.summary }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.ressourceUrl") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ context.ressourceUrl }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.rssAllowed") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span
        ><input type="checkbox" v-model="context.rssAllowed" disabled
      /></span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.startDate") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ formatDate(context.startDate) }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.endDate") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ formatDate(context.endDate) }}</span>
    </dd>
  </dl>
  <dl class="row entity-details" v-if="context.validatedBy">
    <dt class="col-sm-5">
      <span>{{ $t("resource.validatedBy") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ context.validatedBy.displayName }}</span>
    </dd>
  </dl>
  <dl class="row entity-details" v-if="context.validatedDate">
    <dt class="col-sm-5">
      <span>{{ $t("resource.validatedDate") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ formatDate(context.validatedDate) }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.createdBy") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ context.createdBy.displayName }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.createdDate") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ formatDateAction(context.createdDate) }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.lastModifiedBy") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ context.lastModifiedBy.displayName }}</span>
    </dd>
  </dl>
  <dl class="row entity-details">
    <dt class="col-sm-5">
      <span>{{ $t("resource.lastModifiedDate") }}</span>
    </dt>
    <dd class="col-sm-7">
      <span>{{ formatDateAction(context.lastModifiedDate) }}</span>
    </dd>
  </dl>
</template>

<script>
import DateUtils from "@/services/util/DateUtils";
import store from "@/store/index.js";

export default {
  name: "Ressource",
  inject: ["context", "getEnumlabel", "detailSubject", "getUrlEnclosure"],
  methods: {
    formatDate(date) {
      return DateUtils.formatDateToLongIntString(
        date,
        store.getters.getLanguage
      );
    },
    formatDateAction(date) {
      return DateUtils.formatDateTimeToLongIntString(
        date,
        store.getters.getLanguage
      );
    },
    getItemStatusLabel(name) {
      return this.getEnumlabel("itemStatus", name) || "";
    },
  },
};
</script>
