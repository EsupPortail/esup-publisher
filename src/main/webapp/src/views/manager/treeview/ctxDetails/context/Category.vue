<template>
  <div>
    <h3 class="mt-3 mb-2">
      {{ $t("manager.treeview.details.context.properties") }}
    </h3>
    <div class="mb-3">
      <button
        type="button"
        v-can-edit="context.contextKey"
        @click="updateCategory()"
        class="btn btn-primary me-1"
      >
        <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{
          $t("entity.action.edit")
        }}</span>
      </button>
      <button
        type="button"
        @click="deleteCategory()"
        v-can-delete="context.contextKey"
        class="btn btn-danger"
      >
        <span class="far fa-times-circle"></span>&nbsp;<span>{{
          $t("entity.action.delete")
        }}</span>
      </button>
    </div>

    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t("category.name") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.name }}</span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t("category.description") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.description }}</span>
      </dd>
    </dl>
    <dl
      class="row entity-details"
      v-if="
        context.iconUrl &&
        context.iconUrl !== '' &&
        context.iconUrl !== 'http://' &&
        context.publisher &&
        context.publisher.context.reader.classificationDecorations.includes(
          'ENCLOSURE'
        )
      "
    >
      <dt class="col-sm-5">
        <span>{{ $t("category.iconUrl") }}</span>
      </dt>
      <dd class="col-sm-7">
        <img
          class="media-object img-fluid"
          :src="context.iconUrl"
          alt="image"
        />
      </dd>
    </dl>
    <dl
      class="row entity-details"
      v-if="
        context.publisher &&
        context.publisher.context.reader.classificationDecorations.includes(
          'COLOR'
        )
      "
    >
      <dt class="col-sm-5">
        <span>{{ $t("category.color") }}</span>
      </dt>
      <dd class="col-sm-7">
        <div
          v-if="context.color"
          class="classification-color"
          :style="{ 'background-color': context.color }"
        />
        <span v-if="!context.color">{{ $t("category.color-none") }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t("category.accessView") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ $t(getAccessTypeLabel(context.accessView)) }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t("category.rssAllowed") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span
          ><input
            type="checkbox"
            class="input-sm"
            v-model="context.rssAllowed"
            disabled
        /></span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t("category.defaultDisplayOrder") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{
          $t(getDisplayOrderTypeLabel(context.defaultDisplayOrder))
        }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t("category.displayOrder") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.displayOrder }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t("category.lang") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ $t(getLangLabel(context.lang)) }}</span>
      </dd>
    </dl>
    <dl class="row entity-details" v-has-role="'ROLE_ADMIN'">
      <dt class="col-sm-5">
        <span>{{ $t("category.ttl") }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.ttl }}</span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5"><i class="fa fa-rss fa-lg"></i></dt>
      <dd class="col-sm-7">
        <a
          :href="
            appUrl +
            'feed/rss/' +
            (context.publisher
              ? context.publisher.context.organization.id
              : '') +
            '?pid=' +
            (context.publisher ? context.publisher.id : '') +
            '&cid=' +
            context.id
          "
          target="_blank"
        >
          {{ appUrl }}feed/rss/{{
            context.publisher ? context.publisher.context.organization.id : ""
          }}?pid={{ context.publisher ? context.publisher.id : "" }}&cid={{
            context.id
          }}</a
        >
      </dd>
    </dl>

    <CategoryForm ref="categoryForm"></CategoryForm>

    <div class="modal fade" ref="deleteCategoryConfirmation">
      <div class="modal-dialog">
        <div class="modal-content">
          <form name="deleteForm">
            <div class="modal-header">
              <h4 class="modal-title">{{ $t("entity.delete.title") }}</h4>
              <button
                type="button"
                class="btn-close"
                aria-hidden="true"
                data-bs-dismiss="modal"
              ></button>
            </div>
            <div class="modal-body">
              <p>{{ $t("category.delete.question", { id: context.name }) }}</p>
            </div>
            <div class="modal-footer">
              <button
                type="button"
                class="btn btn-default btn-outline-dark"
                data-bs-dismiss="modal"
              >
                <span class="fas fa-ban"></span>&nbsp;<span>{{
                  $t("entity.action.cancel")
                }}</span>
              </button>
              <button
                type="button"
                class="btn btn-danger"
                @click="confirmDelete()"
              >
                <span class="far fa-times-circle"></span>&nbsp;<span>{{
                  $t("entity.action.delete")
                }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CategoryForm from "./CategoryForm.vue";
import { Modal } from "bootstrap";

export default {
  name: "Category",
  components: {
    CategoryForm,
  },
  data() {
    return {
      deleteModal: null,
      categoryForm: null,
    };
  },
  inject: [
    "context",
    "getEnumlabel",
    "updateContext",
    "confirmDeleteContext",
    "appUrl",
  ],
  methods: {
    getAccessTypeLabel(name) {
      return this.getEnumlabel("accessType", name) || "";
    },
    getDisplayOrderTypeLabel(name) {
      return this.getEnumlabel("displayOrderType", name) || "";
    },
    getLangLabel(name) {
      return this.getEnumlabel("lang", name) || "";
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de catégorie
    updateCategory() {
      this.updateContext(() => {
        this.categoryForm.show();
      });
    },
    // Méthode en charge d'ouvrir la modale de suppression de catégorie
    deleteCategory() {
      this.deleteModal.show();
    },
    confirmDelete() {
      this.confirmDeleteContext(() => {
        this.deleteModal.hide();
      });
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteCategoryConfirmation);
    this.categoryForm = this.$refs.categoryForm;
  },
};
</script>
