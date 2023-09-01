<template>
  <div class="publish-publisher">
    <h3>{{ $t('manager.publish.publisher.title') }}</h3>
    <div class="form-group" v-if="publishers && publishers.length >= 1">
      <label class="control-label" for="reader">{{ $t('manager.publish.publisher.reader') }}</label>
      <select
        class="form-select"
        id="reader"
        name="reader"
        v-model="selectedPublisher.context.reader"
        @change="selectReader(selectedPublisher.context.reader)"
        required
      >
        <option v-for="reader in readers" :key="reader.id" :value="reader">
          {{ reader.displayName }}
        </option>
      </select>
    </div>

    <div class="form-group" v-if="selectedPublisher.context.reader && selectedPublisher.context.reader.id && redactors.length > 1">
      <label class="control-label" for="redactor">{{ $t('manager.publish.publisher.redactor') }}</label>
      <select
        class="form-select"
        id="redactor"
        name="redactor"
        v-model="selectedPublisher.context.redactor"
        @change="selectRedactor(selectedPublisher.context.redactor)"
        required
      >
        <option v-for="redactor in redactors" :key="redactor.id" :value="redactor">
          {{ redactor.displayName }}
        </option>
      </select>
    </div>

    <div class="form-group" v-if="selectedPublisher.context.redactor && selectedPublisher.context.redactor.id">
      <label class="control-label" for="organization">{{ $t('manager.publish.publisher.organization') }}</label>
      <select
        class="form-select"
        id="organization"
        name="organization"
        v-model="selectedPublisher.context.organization"
        @change="selectOrganization(selectedPublisher.context.organization)"
        required
      >
        <option v-for="organization in sortedOrganizations" :key="organization.id" :value="organization">
          {{ organization.name }}
        </option>
      </select>
    </div>

    <div v-if="publishers && publishers.length < 1" class="alert alert-danger">
      {{ $t('manager.publish.publisher.emptyList') }}
    </div>

    <div class="text-center">
      <div class="btn-group" role="group">
        <router-link to="/home" custom v-slot="{ navigate }">
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
          </button>
        </router-link>
        <router-link to="classification" custom v-slot="{ navigate }">
          <button
            type="button"
            :disabled="!isPublisherSelected"
            class="btn btn-default btn-outline-dark btn-nav"
            @click="
              validatePublisher();
              navigate();
            "
          >
            <span> {{ $t('manager.publish.nextStep') }}</span
            >&nbsp;<span class="fas fa-arrow-right"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>
<script>
import ClassificationService from '@/services/entities/classification/ClassificationService';
import PublisherService from '@/services/entities/publisher/PublisherService';
import CommonUtils from '@/services/util/CommonUtils';

export default {
  name: 'Publisher',
  data() {
    return {
      publishers: null,
      selectedPublisher: {
        context: { reader: {}, redactor: {}, organization: {} },
      },
      readers: [],
      redactors: [],
      organizations: [],
      filteredFromReader: [],
      filteredFromRedactor: [],
    };
  },
  inject: ['contentData', 'publisher', 'setPublisher'],
  computed: {
    isPublisherSelected() {
      return (
        this.selectedPublisher.context.reader !== null &&
        this.selectedPublisher.context.reader !== undefined &&
        !CommonUtils.equals({}, this.selectedPublisher.context.reader) &&
        this.selectedPublisher.context.redactor !== null &&
        this.selectedPublisher.context.redactor !== undefined &&
        !CommonUtils.equals({}, this.selectedPublisher.context.redactor) &&
        this.selectedPublisher.context.organization !== null &&
        this.selectedPublisher.context.organization !== undefined &&
        !CommonUtils.equals({}, this.selectedPublisher.context.organization)
      );
    },
    sortedOrganizations() {
      var sortedOrganizations = this.organizations;
      sortedOrganizations.sort((org1, org2) => org1.name.localeCompare(org2.name));
      return sortedOrganizations;
    },
  },
  methods: {
    loadAll() {
      PublisherService.query({ used: true })
        .then((result) => {
          this.publishers = result.data;

          this.readers = [];
          this.redactors = [];
          this.organizations = [];
          this.filteredFromReader = [];
          this.filteredFromRedactor = [];

          // init all selectors if new publication
          if (this.publisher === null || this.publisher === undefined || CommonUtils.equals({}, this.publisher)) {
            this.initReaders();
            if (this.publishers.length === 1) {
              this.selectedPublisher.context.reader = Object.assign({}, this.publishers[0].context.reader);
              this.selectedPublisher.context.redactor = Object.assign({}, this.publishers[0].context.redactor);
              this.selectedPublisher.context.organization = Object.assign({}, this.publishers[0].context.organization);
              this.readers = [this.publishers[0].context.reader];
              this.redactors = [this.publishers[0].context.redactor];
              this.organizations = [this.publishers[0].context.organization];
            }
          } else {
            // init selectors from provided publisher
            // we provide all readers as it can be changed and is the first select that will permit filtering other selects
            this.initReaders();
            this.initRedactors(this.publishers, true);
            this.initOrganizations(this.filteredFromReader, true);
          }
        })
        .catch(() => {
          this.publishers = [];
        });
    },
    initReaders() {
      this.readers = [];
      var uniqreaders = {};
      for (var i = 0, size = this.publishers.length; i < size; i++) {
        if (!uniqreaders[this.publishers[i].context.reader.id]) {
          this.readers.push(this.publishers[i].context.reader);
          uniqreaders[this.publishers[i].context.reader.id] = this.publishers[i].context.reader;
        }
      }
    },
    initRedactors(publishers, filterFromPublisherReader) {
      this.redactors = [];
      this.filteredFromReader = [];
      publishers = CommonUtils.isObject(publishers) ? this.toArray(publishers) : publishers;
      if (!CommonUtils.isArray(publishers)) {
        return [];
      }
      if (filterFromPublisherReader && this.selectedPublisher.context.reader && this.selectedPublisher.context.reader.id) {
        this.filteredFromReader = this.filterFromReader(publishers, this.selectedPublisher.context.reader.id);
      } else {
        this.filteredFromReader = Array.from(publishers);
      }
      var uniqredactors = {};
      for (var i = 0, size = this.filteredFromReader.length; i < size; i++) {
        if (!uniqredactors[this.filteredFromReader[i].context.redactor.id]) {
          this.redactors.push(this.filteredFromReader[i].context.redactor);
          uniqredactors[this.filteredFromReader[i].context.redactor.id] = this.filteredFromReader[i].context.redactor;
        }
      }
    },
    initOrganizations(publishers, filterFromPublisherRedactor) {
      this.organizations = [];
      this.filteredFromRedactor = [];
      publishers = CommonUtils.isObject(publishers) ? this.toArray(publishers) : publishers;
      if (!CommonUtils.isArray(publishers)) {
        return [];
      }
      if (filterFromPublisherRedactor && this.selectedPublisher.context.redactor && this.selectedPublisher.context.redactor.id) {
        this.filteredFromRedactor = this.filterFromRedactor(publishers, this.selectedPublisher.context.redactor.id);
      } else {
        this.filteredFromRedactor = Array.from(publishers);
      }
      var uniqorganizations = {};
      for (var i = 0, size = this.filteredFromRedactor.length; i < size; i++) {
        if (!uniqorganizations[this.filteredFromRedactor[i].context.organization.id]) {
          this.organizations.push(this.filteredFromRedactor[i].context.organization);
          uniqorganizations[this.filteredFromRedactor[i].context.organization.id] = this.filteredFromRedactor[i].context.organization;
        }
      }
    },
    filterFromReader(collection, search) {
      search = typeof search === 'string' || typeof search === 'number' ? String(search).toLowerCase() : undefined;

      collection = CommonUtils.isObject(collection) ? this.toArray(collection) : collection;

      if (!CommonUtils.isArray(collection) || typeof search === 'undefined') {
        return [];
      }
      var filtered = [];
      for (var i = 0, size = collection.length; i < size; i++) {
        if (collection[i].context.reader.id === parseInt(search)) {
          filtered.push(Object.assign({}, collection[i]));
        }
      }
      return filtered;
    },
    filterFromRedactor(collection, search) {
      search = typeof search === 'string' || typeof search === 'number' ? String(search).toLowerCase() : undefined;

      collection = CommonUtils.isObject(collection) ? this.toArray(collection) : collection;

      if (!CommonUtils.isArray(collection) || typeof search === 'undefined') {
        return [];
      }
      var filtered = [];
      for (var i = 0, size = collection.length; i < size; i++) {
        if (collection[i].context.redactor.id === parseInt(search)) {
          filtered.push(Object.assign({}, collection[i]));
        }
      }
      return filtered;
    },
    selectReader(item) {
      this.selectedPublisher.context.reader = item;

      this.initRedactors(this.publishers, true);
      if (this.filteredFromReader.length === 1) {
        this.selectedPublisher.context.redactor = Object.assign({}, this.filteredFromReader[0].context.redactor);
        this.selectedPublisher.context.organization = Object.assign({}, this.filteredFromReader[0].context.organization);
        this.redactors = [this.filteredFromReader[0].context.redactor];
        this.organizations = [this.filteredFromReader[0].context.organization];
      } else if (this.redactors.length === 1) {
        this.selectRedactor(this.redactors[0]);
      } else {
        this.selectedPublisher.context.redactor = {};
        this.selectedPublisher.context.organization = {};
      }
    },
    selectRedactor(item) {
      this.selectedPublisher.context.redactor = item;

      this.initOrganizations(this.filteredFromReader, true);
      if (this.filteredFromRedactor.length === 1) {
        this.selectedPublisher.context.organization = Object.assign({}, this.filteredFromRedactor[0].context.organization);
        this.organizations = [this.filteredFromReader[0].context.organization];
      } else {
        this.selectedPublisher.context.organization = {};
      }
    },
    selectOrganization(item) {
      this.selectedPublisher.context.organization = item;
    },
    validatePublisher() {
      if (this.isPublisherSelected) {
        for (var i = 0, size = this.publishers.length; i < size; i++) {
          if (
            this.publishers[i].context.reader.id === this.selectedPublisher.context.reader.id &&
            this.publishers[i].context.redactor.id === this.selectedPublisher.context.redactor.id &&
            this.publishers[i].context.organization.id === this.selectedPublisher.context.organization.id
          ) {
            this.setPublisher(Object.assign({}, this.publishers[i]));
            break;
          }
        }
      }
    },
    toArray(object) {
      return CommonUtils.isArray(object)
        ? object
        : Object.keys(object).map((key) => {
            return object[key];
          });
    },
  },
  created() {
    if (this.publisher === null || this.publisher === undefined || CommonUtils.equals({}, this.publisher)) {
      if (this.contentData && this.contentData.classifications && this.contentData.classifications.length > 0) {
        ClassificationService.get(this.contentData.classifications[0].keyId).then((response) => {
          if (response.data) {
            this.setPublisher(response.data.publisher);
            this.selectedPublisher = {
              context: {
                reader: this.publisher.context.reader,
                redactor: this.publisher.context.redactor,
                organization: this.publisher.context.organization,
              },
            };
          }
        });
      }
    } else {
      this.selectedPublisher = {
        context: {
          reader: this.publisher.context.reader,
          redactor: this.publisher.context.redactor,
          organization: this.publisher.context.organization,
        },
      };
    }
    this.loadAll();
  },
};
</script>
