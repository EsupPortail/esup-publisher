<template>
  <div v-if="showCats" class="publish-classification">
    <h3>{{ $t('manager.publish.classification.title') }}</h3>

    <div class="classification-form">
      <div v-if="classificationsList.length > 0" class="form-group">
        <label class="control-label">{{ $t('manager.publish.classification.choice') }}</label>
        <div class="inline-form">
          <div v-for="classif in classificationsList" :key="classif.name" class="form-check form-check-inline">
            <input
              class="form-check-input"
              :id="classif.name"
              type="checkbox"
              :value="classif.name"
              :checked="containsSelectedClassification(classif.contextKey)"
              @click="toggleSelection(classif.contextKey)"
              :class="{
                iconurl: inArray('ENCLOSURE', publisher.context.reader.classificationDecorations),
              }"
            />
            <label class="form-check-label" :for="classif.name" v-tooltip="classif.description">
              <span
                class="iconurl"
                :style="{
                  'background-image': inArray('ENCLOSURE', publisher.context.reader.classificationDecorations)
                    ? 'url(' + classif.icon + ')'
                    : null,
                }"
                v-if="inArray('ENCLOSURE', publisher.context.reader.classificationDecorations)"
              ></span>
              <span
                class="pastille me-1"
                :style="{
                  'background-color': inArray('COLOR', publisher.context.reader.classificationDecorations) ? classif.color : null,
                }"
                v-if="inArray('COLOR', publisher.context.reader.classificationDecorations)"
              ></span>
              <span clas="text">{{ classif.name }}</span>
            </label>
          </div>
        </div>
      </div>
      <div v-if="classificationsList.length < 1" class="alert alert-danger">
        <span>{{ $t('manager.publish.classification.emptyList') }}</span>
      </div>
      <div v-if="classificationsList.length > 0 && hasHighlightCats()" class="form-group" v-can-highlight="publisher.contextKey">
        <label class="control-label"
          ><span>{{
            $t('manager.publish.classification.highlight', {
              name: classificationHighlighted.name,
            })
          }}</span>
          <input type="checkbox" class="form-check-input" name="highlight" id="highlight" v-model="_highlight" />
        </label>
      </div>
    </div>

    <div class="text-center">
      <div class="btn-group" role="group">
        <router-link to="publisher" custom v-slot="{ navigate }">
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-arrow-left"></span>&nbsp;<span> {{ $t('entity.action.back') }}</span>
          </button>
        </router-link>
        <router-link to="/home" custom v-slot="{ navigate }">
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-times"></span>&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
          </button>
        </router-link>
        <router-link to="content" custom v-slot="{ navigate }">
          <button
            type="button"
            :disabled="classifications === null || classifications === undefined || classifications.length < 1"
            class="btn btn-default btn-outline-dark btn-nav"
            @click="navigate"
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
import CommonUtils from '@/services/util/CommonUtils';

export default {
  name: 'Classification',
  data() {
    return {
      showCats: false,
      classificationsList: [],
      classificationHighlighted: {},
    };
  },
  inject: ['contentData', 'publisher', 'classifications', 'setClassifications', 'item', 'highlight', 'setHighlight'],
  computed: {
    _highlight: {
      get() {
        return this.highlight;
      },
      set(newVal) {
        this.setHighlight(newVal);
      },
    },
  },
  methods: {
    loadAll() {
      ClassificationService.query(this.publisher.id, true).then((response) => {
        response.data.forEach((obj) => {
          this.classificationsList.push({
            contextKey: obj.contextKey,
            icon: obj.iconUrl,
            name: obj.name,
            description: obj.description,
            color: obj.color,
          });
        });
        // we clean already saved classification where user loose rights
        if (this.contentData && this.contentData.classifications) {
          this.contentData.classifications.forEach((obj) => {
            var found = false;
            this.classificationsList.forEach((objList) => {
              if (CommonUtils.equals(objList.contextKey, obj)) {
                found = true;
              }
            });
            if (!found) {
              this.remove(obj);
            }
          });
        }
        // if only one Classification is enable automatic select it !
        if (this.classificationsList.length === 1 && !this.inArray(this.classificationsList[0].contextKey, this.classifications)) {
          const newClassifications = Array.from(this.classifications || []);
          newClassifications.push(this.classificationsList[0].contextKey);
          this.setClassifications(newClassifications);
        }
        this.autoValidateClassif();
        this.showCats = true;
      });

      ClassificationService.highlighted().then((response) => {
        this.classificationHighlighted = response.data;
      });
    },
    autoValidateClassif() {
      if (
        this.publisher.context.redactor.writingMode === 'STATIC' &&
        this.inArray('FLASH', this.publisher.context.reader.authorizedTypes) &&
        this.classifications !== null &&
        this.classifications !== undefined &&
        this.classifications.length > 0
      ) {
        this.$router.push({ path: 'content' });
      }
    },
    hasHighlightCats() {
      return this.publisher.doHighlight;
    },
    containsSelectedClassification(contextKey) {
      return this.inArray(contextKey, this.classifications);
    },
    toggleSelection(contextKey) {
      var i = 0;
      var idx = -1;
      if (this.classifications !== null && this.classifications !== undefined) {
        for (var size = this.classifications.length; i < size; i++) {
          if (CommonUtils.equals(this.classifications[i], contextKey)) {
            idx = i;
            break;
          }
        }
      }
      const newClassifications = Array.from(this.classifications || []);
      if (idx > -1) {
        // is currently selected
        newClassifications.splice(idx, 1);
      } else {
        // is newly selected
        newClassifications.push(contextKey);
      }
      this.setClassifications(newClassifications);
    },
    remove(contextkey) {
      this.setClassifications(
        this.classifications.filter((element) => {
          return element !== contextkey;
        }),
      );
    },
    inArray(item, array) {
      if (array === null || array === undefined || !CommonUtils.isArray(array) || array.length < 1) return false;
      for (var i = 0, size = array.length; i < size; i++) {
        if (CommonUtils.equals(array[i], item)) {
          return true;
        }
      }
      return false;
    },
  },
  created() {
    // Si aucun publisher de saisie, on redirige vers la page Publisher
    if (this.publisher === null || this.publisher === undefined) {
      this.$router.push({ path: 'publisher' });
    } else {
      if (
        (this.classifications === null || this.classifications === undefined || CommonUtils.equals([], this.classifications)) &&
        this.contentData &&
        this.contentData.classifications
      ) {
        this.setClassifications(Array.from(this.contentData.classifications));
      }

      if (
        (this.item === null || !CommonUtils.isDefined(this.item) || CommonUtils.equals({}, this.item)) &&
        this.contentData &&
        this.contentData.item
      ) {
        this.setHighlight(this.contentData.item.highlight);
      }

      this.loadAll();
    }
  },
};
</script>
