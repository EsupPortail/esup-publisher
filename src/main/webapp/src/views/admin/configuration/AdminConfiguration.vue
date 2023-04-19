<template>
  <div>
    <h2>{{ $t("configuration.title") }}</h2>

    {{ $t("configuration.filter") }}
    <input type="text" v-model="filter" class="form-control" />

    <table
      class="table table-sm table-striped table-bordered table-responsive"
      style="table-layout: fixed"
    >
      <thead>
        <tr>
          <th @click="setSorting('prefix')">
            {{ $t("configuration.table.prefix") }}
          </th>
          <th>{{ $t("configuration.table.properties") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="entry in filteredConfiguration" :key="entry.prefix">
          <td>
            <span>{{ entry.prefix }}</span>
          </td>
          <td>
            <div
              class="row"
              v-for="(value, key) in entry.properties"
              :key="key"
            >
              <div class="col-md-6">{{ key }}</div>
              <div class="col-md-6">
                <span
                  class="float-end badge bg-info"
                  style="white-space: normal; word-break: break-all"
                  >{{ value }}</span
                >
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import ConfigurationService from "@/services/admin/ConfigurationService";

export default {
  name: "AdminConfiguration",
  data() {
    return {
      // Configuration
      configuration: [],
      // Filtre appliqué sur les loggers
      filter: null,
      // Sens du tri des loggers
      reverse: false,
      // Propriété des loggers sur laquelle le tri est effectué
      predicate: null,
    };
  },
  computed: {
    filteredConfiguration() {
      var filterConfiguration = this.configuration;

      // Filtre des loggers
      if (this.filter !== null && this.filter !== "") {
        filterConfiguration = filterConfiguration.filter(
          (conf) =>
            conf.prefix.includes(this.filter) ||
            JSON.stringify(conf.properties).includes(this.filter)
        );
      }

      // Tri des loggers
      if (this.predicate !== null) {
        filterConfiguration.sort(
          (conf1, conf2) =>
            conf1[this.predicate].localeCompare(conf2[this.predicate]) *
            (this.reverse ? -1 : 1)
        );
      }

      return filterConfiguration;
    },
  },
  methods: {
    setSorting(predicate) {
      this.predicate = predicate;
      this.reverse = !this.reverse;
    },
    getConfigPropertiesObjects(res) {
      return res[0].application.beans;
    },
  },
  created() {
    ConfigurationService.get().then((configuration) => {
      var properties = [];
      var propertiesObject = this.getConfigPropertiesObjects(configuration);
      Object.keys(propertiesObject).forEach((key) => {
        if (Object.prototype.hasOwnProperty.call(propertiesObject, key)) {
          properties.push(propertiesObject[key]);
        }
      });
      this.configuration = this.configuration.concat(properties);
    });
    ConfigurationService.getEnv().then((response) => {
      var properties = [];
      var propertiesObject = response.data.propertySources;
      propertiesObject.forEach((obj) => {
        var name = obj.name;
        var detailProperties = obj.properties;
        var vals = {};
        Object.keys(detailProperties).forEach((key) => {
          vals[key] = detailProperties[key].value;
        });
        properties.push({ prefix: name, properties: vals });
      });
      this.configuration = this.configuration.concat(properties);
    });
  },
};
</script>
