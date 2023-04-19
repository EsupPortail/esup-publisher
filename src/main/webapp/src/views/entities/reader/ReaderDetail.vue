<template>
  <div>
    <h2>
      <span>{{ $t("reader.detail.title") }}</span> {{ $route.params.id }}
    </h2>
    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t("entity.detail.field") }}</th>
            <th>{{ $t("entity.detail.value") }}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <span>{{ $t("reader.name") }}</span>
            </td>
            <td>
              <input
                type="text"
                class="input-sm form-control"
                :value="reader.name"
                readonly
              />
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t("reader.displayName") }}</span>
            </td>
            <td>
              <input
                type="text"
                class="input-sm form-control"
                :value="reader.displayName"
                readonly
              />
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t("reader.description") }}</span>
            </td>
            <td>
              <input
                type="text"
                class="input-sm form-control"
                :value="reader.description"
                readonly
              />
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t("reader.authorizedTypes") }}</span>
            </td>
            <td>
              <input
                type="text"
                class="input-sm form-control"
                v-for="type in reader.authorizedTypes"
                :key="type"
                :value="$t('enum.itemType.' + type)"
                readonly
              />
            </td>
          </tr>
          <tr>
            <td>
              <span>{{ $t("reader.classificationDecorations") }}</span>
            </td>
            <td>
              <input
                type="text"
                class="input-sm form-control"
                v-for="type in reader.classificationDecorations"
                :key="type"
                :value="$t('enum.classificationDecorType.' + type)"
                readonly
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <button type="submit" @click="readerPage" class="btn btn-info">
      <span class="fas fa-arrow-left"></span>&nbsp;<span>
        {{ $t("entity.action.back") }}</span
      >
    </button>
  </div>
</template>

<script>
import ReaderService from "@/services/entities/reader/ReaderService";

export default {
  name: "ReaderDetail",
  data() {
    return {
      reader: {
        name: null,
        displayName: null,
        description: null,
        id: null,
        authorizedTypes: [],
        classificationDecorations: [],
      },
    };
  },
  methods: {
    // Méthode de récupération de l'objet grâce à l'id passé en paramètre
    initData() {
      ReaderService.get(this.$route.params.id)
        .then((response) => {
          this.reader = response.data;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    // Méthode de redirection sur la page listant les lecteurs
    readerPage() {
      this.$router.push({ name: "AdminEntityReader" });
    },
  },
  created() {
    this.initData();
  },
};
</script>
