<template>
  <div
    class="modal fade"
    id="saveCategoryModal"
    tabindex="-1"
    role="dialog"
    aria-labelledby="myCategoryLabel"
    aria-hidden="true"
    ref="saveCategoryModal"
  >
    <div class="modal-dialog modal-fullscreen-md-down modal-lg">
      <div class="modal-content">
        <form
          name="editForm"
          role="form"
          class="was-validated"
          novalidate
          show-validation
        >
          <div class="modal-header">
            <h4 class="modal-title" id="myCategoryLabel">
              {{ $t("category.home.createOrEditLabel") }}
            </h4>
            <button
              type="button"
              class="btn-close"
              aria-hidden="true"
              data-bs-dismiss="modal"
            ></button>
          </div>
          <div class="modal-body">
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label class="control-label" for="ID">ID</label>
              <input
                type="text"
                class="form-control"
                name="id"
                id="ID"
                v-model="editedContext.id"
                disabled
              />
            </div>
            <div class="form-group">
              <label for="name" class="control-label">{{
                $t("category.name")
              }}</label>
              <input
                type="text"
                class="form-control"
                name="name"
                id="name"
                v-model="editedContextName"
                required
                minlength="3"
                maxlength="200"
              />
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('name', formErrors.REQUIRED)"
              >
                {{ $t("entity.validation.required") }}
              </div>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)"
              >
                {{ $t("entity.validation.minlength", { min: "3" }) }}
              </div>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)"
              >
                {{ $t("entity.validation.maxlength", { max: "200" }) }}
              </div>
            </div>
            <div class="form-group">
              <label for="description" class="control-label">{{
                $t("category.description")
              }}</label>
              <input
                type="text"
                class="form-control"
                name="description"
                id="description"
                v-model="editedContextDescription"
                required
                minlength="5"
                maxlength="512"
              />
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError('description', formErrors.REQUIRED)
                "
              >
                {{ $t("entity.validation.required") }}
              </div>
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError('description', formErrors.MIN_LENGTH)
                "
              >
                {{ $t("entity.validation.minlength", { min: "5" }) }}
              </div>
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError('description', formErrors.MAX_LENGTH)
                "
              >
                {{ $t("entity.validation.maxlength", { max: "512" }) }}
              </div>
            </div>
            <div
              class="form-group"
              v-if="
                publisher.context.reader.classificationDecorations &&
                publisher.context.reader.classificationDecorations.includes(
                  'ENCLOSURE'
                )
              "
            >
              <label class="control-label" for="iconUrl">{{
                $t("category.iconUrl")
              }}</label>
              <input
                type="url"
                class="form-control"
                name="iconUrl"
                id="iconUrl"
                placeholder="http://..."
                v-model="editedContextIconUrl"
                maxlength="2048"
                ref="iconUrlInput"
              />
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('iconUrl', formErrors.REQUIRED)"
              >
                {{ $t("entity.validation.required") }}
              </div>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('iconUrl', formErrors.MAX_LENGTH)"
              >
                {{ $t("entity.validation.maxlength", { max: "2048" }) }}
              </div>
              <div class="invalid-feedback" v-if="invalidIconUrl">
                {{ $t("entity.validation.url") }}
              </div>
            </div>
            <div
              class="form-group"
              v-if="
                publisher.context.reader.classificationDecorations &&
                publisher.context.reader.classificationDecorations.includes(
                  'COLOR'
                )
              "
            >
              <label for="color" class="control-label">{{
                $t("category.color")
              }}</label>
              <esup-color-palette-picker
                id="color"
                .color="editedContext.color"
                .config="colorPickerConfig"
                .onColorChanged="onColorChanged"
              ></esup-color-palette-picker>
            </div>
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label for="accessView" class="control-label">{{
                $t("category.accessView")
              }}</label>
              <select
                class="form-select"
                id="accessView"
                name="accessView"
                v-model="editedContextAccessView"
                required
              >
                <option
                  v-for="accessType in accessTypeList"
                  :key="accessType.id"
                  :value="accessType.name"
                >
                  {{ $t(accessType.label) }}
                </option>
              </select>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('accessView', formErrors.REQUIRED)"
              >
                {{ $t("entity.validation.required") }}
              </div>
            </div>
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label for="rssAllowed" class="control-label">{{
                $t("category.rssAllowed")
              }}</label>
              <input
                type="checkbox"
                class="form-check-input d-block mx-auto"
                name="rssAllowed"
                id="rssAllowed"
                v-model="editedContextRssAllowed"
              />
            </div>
            <div class="form-group">
              <label for="defaultDisplayOrder" class="control-label">{{
                $t("category.defaultDisplayOrder")
              }}</label>
              <select
                class="form-select"
                id="defaultDisplayOrder"
                name="defaultDisplayOrder"
                v-model="editedContextDefaultDisplayOrder"
                required
                @change="onChangeDefaultDisplayOrder()"
              >
                <option
                  v-for="displayOrderType in autorizedDisplayOrderTypeList"
                  :key="displayOrderType.id"
                  :value="displayOrderType.name"
                >
                  {{ $t(displayOrderType.label) }}
                </option>
              </select>
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError(
                    'defaultDisplayOrder',
                    formErrors.REQUIRED
                  )
                "
              >
                {{ $t("entity.validation.required") }}
              </div>
            </div>
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label for="lang" class="control-label">{{
                $t("category.lang")
              }}</label>
              <select
                class="form-select"
                id="lang"
                name="name"
                v-model="editedContextLang"
                required
              >
                <option
                  v-for="lang in langList"
                  :key="lang.id"
                  :value="lang.id"
                >
                  {{ $t(lang.label) }}
                </option>
              </select>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('lang', formErrors.REQUIRED)"
              >
                {{ $t("entity.validation.required") }}
              </div>
            </div>
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label for="ttl" class="control-label">{{
                $t("category.ttl")
              }}</label>
              <input
                type="number"
                class="form-control"
                name="ttl"
                id="ttl"
                v-model="editedContextTtl"
                required
                min="900"
                max="86400"
              />
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('ttl', formErrors.REQUIRED)"
              >
                {{ $t("entity.validation.required") }}
              </div>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('ttl', formErrors.MIN_VALUE)"
              >
                {{ $t("entity.validation.min", { min: "900" }) }}
              </div>
              <div
                class="invalid-feedback"
                v-if="formValidator.hasError('ttl', formErrors.MAX_VALUE)"
              >
                {{ $t("entity.validation.max", { max: "86400" }) }}
              </div>
            </div>
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label for="displayOrder" class="control-label">{{
                $t("category.displayOrder")
              }}</label>
              <input
                type="number"
                class="form-control"
                name="displayOrder"
                id="displayOrder"
                v-model="editedContextDisplayOrder"
                required
                min="0"
                max="999"
                :disabled="editedContext.defaultDisplayOrder !== 'CUSTOM'"
              />
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError('displayOrder', formErrors.REQUIRED)
                "
              >
                {{ $t("entity.validation.required") }}
              </div>
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError('displayOrder', formErrors.MIN_VALUE)
                "
              >
                {{ $t("entity.validation.min", { min: "0" }) }}
              </div>
              <div
                class="invalid-feedback"
                v-if="
                  formValidator.hasError('displayOrder', formErrors.MAX_VALUE)
                "
              >
                {{ $t("entity.validation.max", { max: "999" }) }}
              </div>
            </div>
            <div class="form-group" v-has-role="'ROLE_ADMIN'">
              <label class="control-label" for="publisher">{{
                $t("category.publisher")
              }}</label>
              <input
                type="text"
                class="form-control"
                name="publisher"
                id="publisher"
                :value="
                  editedContext && editedContext.publisher
                    ? editedContext.publisher.context.reader.displayName +
                      '-' +
                      editedContext.publisher.context.redactor.displayName
                    : ''
                "
                disabled
              />
            </div>
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
              class="btn btn-primary"
              :disabled="
                formValidator.hasError() || invalidIconUrl || !canManage
              "
              @click="confirmUpdate"
            >
              <span class="fas fa-download"></span>&nbsp;<span>{{
                $t("entity.action.save")
              }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script>
import {
  FormValidationUtils,
  FormErrorType,
} from "@/services/util/FormValidationUtils";
import EnumDatasService from "@/services/entities/enum/EnumDatasService";
import store from "@/store/index.js";
import { Modal } from "bootstrap";

export default {
  name: "CategoryForm",
  data() {
    return {
      colorPickerConfig: {
        colors: [],
        lang: store.getters.getLanguage,
      },
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
      iconUrlInput: null,
      invalidIconUrl: false,
    };
  },
  inject: [
    "publisher",
    "editedContext",
    "setEditedContext",
    "confirmUpdateContext",
    "canManage",
    "paletteColorPicker",
    "autorizedDisplayOrderTypeList",
    "langList",
  ],
  computed: {
    editedContextName: {
      get() {
        return this.editedContext.name || "";
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.name = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextDescription: {
      get() {
        return this.editedContext.description || "";
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.description = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextIconUrl: {
      get() {
        return this.editedContext.iconUrl || "";
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.iconUrl = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextAccessView: {
      get() {
        return this.editedContext.accessView || "";
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.accessView = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextRssAllowed: {
      get() {
        return this.editedContext.rssAllowed || false;
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.rssAllowed = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextDefaultDisplayOrder: {
      get() {
        return this.editedContext.defaultDisplayOrder || "";
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.defaultDisplayOrder = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextLang: {
      get() {
        return this.editedContext.lang || "";
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.lang = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextTtl: {
      get() {
        return this.editedContext.ttl || 0;
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.ttl = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    editedContextDisplayOrder: {
      get() {
        return this.editedContext.displayOrder || 0;
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext);
        newEditedContext.displayOrder = newVal;
        this.setEditedContext(newEditedContext);
      },
    },
    accessTypeList() {
      return EnumDatasService.getAccessTypeList();
    },
  },
  methods: {
    show() {
      this.updateModal.show();
    },
    onColorChanged(color) {
      const newEditedContext = Object.assign({}, this.editedContext);
      newEditedContext.color = color;
      this.setEditedContext(newEditedContext);
    },
    confirmUpdate() {
      this.confirmUpdateContext("CATEGORY", () => {
        this.updateModal.hide();
      });
    },
    initFormValidator() {
      this.formValidator.clear();
      this.formValidator.checkTextFieldValidity(
        "name",
        this.editedContext.name,
        3,
        200,
        true
      );
      this.formValidator.checkTextFieldValidity(
        "description",
        this.editedContext.description,
        5,
        512,
        true
      );
      this.formValidator.checkTextFieldValidity(
        "iconUrl",
        this.editedContext.iconUrl,
        null,
        2048,
        false
      );
      this.formValidator.checkTextFieldValidity(
        "accessView",
        this.editedContext.accessView,
        null,
        null,
        true
      );
      this.formValidator.checkTextFieldValidity(
        "defaultDisplayOrder",
        this.editedContext.defaultDisplayOrder,
        null,
        null,
        true
      );
      this.formValidator.checkTextFieldValidity(
        "lang",
        this.editedContext.lang,
        null,
        null,
        true
      );
      this.formValidator.checkNumberFieldValidity(
        "ttl",
        this.editedContext.ttl,
        900,
        86400,
        true
      );
      this.formValidator.checkNumberFieldValidity(
        "displayOrder",
        this.editedContext.displayOrder,
        0,
        999,
        true
      );
    },
    onChangeDefaultDisplayOrder() {
      // Au changement de type d'ordre d'affichage de la catégory, si celui-ce est différent de CUSTOM
      // et que l'ordre d'affichage est invalide, on le passe à 0
      if (
        this.editedContext.defaultDisplayOrder !== "CUSTOM" &&
        this.formValidator.hasError("displayOrder")
      ) {
        this.editedContextDisplayOrder = 0;
      }
    },
  },
  mounted() {
    this.updateModal = new Modal(this.$refs.saveCategoryModal);
    this.iconUrlInput = this.$refs.iconUrlInput;

    // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
    this.invalidIconUrl =
      this.iconUrlInput &&
      !this.formValidator.hasError("iconUrl") &&
      !this.iconUrlInput.checkValidity();
  },
  created() {
    this.colorPickerConfig.colors = this.paletteColorPicker;
  },
  watch: {
    "editedContext.name"(newVal) {
      this.formValidator.checkTextFieldValidity("name", newVal, 3, 200, true);
    },
    "editedContext.description"(newVal) {
      this.formValidator.checkTextFieldValidity(
        "description",
        newVal,
        5,
        512,
        true
      );
    },
    "editedContext.iconUrl"(newVal) {
      this.formValidator.checkTextFieldValidity(
        "iconUrl",
        newVal,
        null,
        2048,
        false
      );
      // Si on n'a pas détecté d'erreur mais que l'input est invalide, alors c'est que le texte saisie n'est pas une url
      this.invalidIconUrl =
        this.iconUrlInput &&
        !this.formValidator.hasError("iconUrl") &&
        !this.iconUrlInput.checkValidity();
    },
    "editedContext.accessView"(newVal) {
      this.formValidator.checkTextFieldValidity(
        "accessView",
        newVal,
        null,
        null,
        true
      );
    },
    "editedContext.defaultDisplayOrder"(newVal) {
      this.formValidator.checkTextFieldValidity(
        "defaultDisplayOrder",
        newVal,
        null,
        null,
        true
      );
    },
    "editedContext.lang"(newVal) {
      this.formValidator.checkTextFieldValidity(
        "lang",
        newVal,
        null,
        null,
        true
      );
    },
    "editedContext.ttl"(newVal) {
      this.formValidator.checkNumberFieldValidity(
        "ttl",
        newVal,
        900,
        86400,
        true
      );
    },
    "editedContext.displayOrder"(newVal) {
      this.formValidator.checkNumberFieldValidity(
        "displayOrder",
        newVal,
        0,
        999,
        true
      );
    },
  },
};
</script>
