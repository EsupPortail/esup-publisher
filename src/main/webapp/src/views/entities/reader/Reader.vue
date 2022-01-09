<template>
<div class='reader'>
    <h2 >{{$t('reader.home.title')}}</h2>
    <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#saveReaderModal" @click="clear()">
        <span class="fas fa-bolt"></span> <span>{{$t('reader.home.createLabel')}}</span>
    </button>
    <div class="modal fade" id="saveReaderModal" tabindex="-1" role="dialog" aria-labelledby="myReaderLabel"
         aria-hidden="true" ref="saveReaderModal">
        <div class="modal-dialog modal-fullscreen-md-down modal-lg">
            <div class="modal-content">
                <form name="editForm" role="form" novalidate show-validation>

                    <div class="modal-header">
                        <h4 class="modal-title" id="myReaderLabel">{{$t('reader.home.createOrEditLabel')}}</h4>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"
                                @click="clear()"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="control-label" for="ID">ID</label>
                            <input type="text" class="form-control" name="id" id="ID"
                                   v-model="reader.id" disabled>
                        </div>

                        <div class="form-group">
                            <label for ="name" class="control-label">{{$t('reader.name')}}</label>
                            <input type="text" class="form-control" :class="(nameMinLength || nameMaxLength) ? 'is-invalid' : 'valid'" name="name" id="name"
                                   v-model="reader.name">
                            <div>
                                <p class="help-block"
                                   v-if="nameFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="nameMinLength">
                                    {{$t('entity.validation.minlength', {min:'3'})}}
                                </p>
                                <p class="help-block"
                                   v-if="nameMaxLength" >
                                    {{$t('entity.validation.maxlength', {max:'20'})}}
                                </p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for ="displayName" class="control-label">{{$t('reader.displayName')}}</label>
                            <input type="text" class="form-control" :class="(displayNameMinLength || displayNameMaxLength) ? 'is-invalid' : 'valid'" name="displayName" id="displayName"
                                   v-model="reader.displayName">

                            <div>
                                <p class="help-block"
                                   v-if="displayNameFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="displayNameMinLength">
                                    {{$t('entity.validation.minlength', {min:'3'})}}
                                </p>
                                <p class="help-block"
                                   v-if="displayNameMaxLength" >
                                    {{$t('entity.validation.maxlength', {max:'50'})}}
                                </p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="description" class="control-label">{{$t('reader.description')}}</label>
                            <input type="text" class="form-control" :class="(descriptionMinLength || descriptionMaxLength) ? 'is-invalid' : 'valid'" name="description" id="description"
                                   v-model="reader.description" required>

                            <div>
                                <p class="help-block"
                                   v-if="descriptionFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="descriptionMinLength">
                                    {{$t('entity.validation.minlength', {min:'5'})}}
                                </p>
                                <p class="help-block"
                                   v-if="descriptionMaxLength">
                                    {{$t('entity.validation.maxlength', {max:'512'})}}
                                </p>
                            </div>
                        </div>

                        <div class="form-group" :class="{ 'has-error': reader.authorizedTypes.length < 1 && authorizedTypesDirty }">
                            <label class="control-label" for="authorizedTypes">{{$t('reader.authorizedTypes')}}</label>
                            <div class="inline-form">
                                <div v-for="authorizedType in authorizedTypesList" :key="authorizedType" class="form-check form-check-inline">
                                    <input :id="authorizedType" class="form-check-input" type="checkbox"  name="authorizedTypes" :value="authorizedType" :checked="containsSelectedType(authorizedType)"
                                           @click="toggleSelectionType(authorizedType)">
                                    <label :for="authorizedType" class="form-check-label">
                                        <span>{{ $t('enum.itemType.' + authorizedType)}}</span>
                                    </label>
                                </div>
                            </div>

                            <div v-if="reader.authorizedTypes.length < 1">
                                <p class="help-block"
                                   v-if="reader.authorizedTypes.length < 1">
                                    {{ $t('entity.validation.required')}}
                                </p>
                            </div>
                        </div>

                        <div class="form-group" :class="{ 'has-error': reader.classificationDecorations.length < 1 && classificationDecorTypesDirty }">
                            <label class="control-label" for="classificationDecorations">{{$t('reader.classificationDecorations')}}</label>
                            <div class="inline-form">
                                <div v-for="classificationDecoration in classificationDecorationsList" :key="classificationDecoration" class="form-check form-check-inline">
                                    <input :id="classificationDecoration" class="form-check-input" type="checkbox"  name="classificationDecorations" :value="classificationDecoration" :checked="containsSelectedDecorType(classificationDecoration)"
                                           @click="toggleSelectionDecorType(classificationDecoration)">
                                    <label :for="classificationDecoration" class="form-check-label">
                                        <span>{{ $t('enum.classificationDecorType.' + classificationDecoration)}}</span>
                                    </label>
                                </div>
                            </div>

                            <div v-if="reader.classificationDecorations.length < 1">
                                <p class="help-block"
                                   v-if="reader.classificationDecorations.length < 1">
                                    {{ $t('entity.validation.required')}}
                                </p>
                            </div>
                        </div>

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear()">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-primary" :class="isAnyFieldError" @click="createReader" >
                            <span class="fas fa-download"></span>&nbsp;<span>{{$t('entity.action.save')}}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" ref="deleteReaderConfirmation">
        <div class="modal-dialog">
            <div class="modal-content">
                <form name="deleteForm">
                    <div class="modal-header">
                        <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <p>{{$t('reader.delete.question', {id: reader.id})}}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-danger" @click="confirmDelete(reader.id)">
                            <span class="far fa-times-circle"></span>&nbsp;<span>{{$t('entity.action.delete')}}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>{{$t('reader.name')}}</th>
                    <th>{{$t('reader.displayName')}}</th>
                    <th>{{$t('reader.description')}}</th>
                    <th>{{$t('reader.authorizedTypes')}}</th>
                    <th>{{$t('reader.classificationDecorations')}}</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="reader in readers" :key="reader.id">
                    <td><router-link :to="{ name: 'AdminEntityReaderDetails', params: { id: reader.id }}">
                    {{reader.id}}</router-link></td>
                    <td>{{reader.name}}</td>
                    <td>{{reader.displayName}}</td>
                    <td>{{reader.description}}</td>
                    <td><span class="list-comma" v-for="type in reader.authorizedTypes" :key="type">{{$t('enum.itemType.' + type)}}</span></td>
                    <td><span class="list-comma" v-for="type in reader.classificationDecorations" :key="type">{{$t('enum.classificationDecorType.' + type)}}</span></td>
                    <td>
                        <button type="submit"
                                @click="readerDetail(reader.id)"
                                class="btn btn-info btn-sm me-1">
                            <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
                        </button>
                        <button type="submit"
                                @click="update(reader.id)"
                                class="btn btn-primary btn-sm me-1">
                            <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
                        </button>
                        <button type="submit"
                                @click="deleteReader(reader.id)"
                                class="btn btn-danger btn-sm">
                            <span class="far fa-times-circle"></span>&nbsp;<span>{{$t("entity.action.delete")}}</span>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
</template>

<script>
import ReaderService from '@/services/entities/reader/ReaderService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import { Modal } from 'bootstrap'

export default {
  name: 'Reader',
  components: {
  },
  data () {
    return {
      readers: [],
      reader: { name: null, displayName: null, description: null, id: null, authorizedTypes: [], classificationDecorations: [] },
      deleteModal: null,
      updateModal: null,
      errors: new Map(),
      authorizedTypesDirty: false,
      classificationDecorTypesDirty: false
    }
  },
  computed: {
    // Liste des types de contenu
    authorizedTypesList () {
      return EnumDatasService.getItemTypeList()
    },
    // Liste des types de contenu
    classificationDecorationsList () {
      return EnumDatasService.getClassificationDecorTypeList()
    },
    // Méthodes en charge du lancement des messages d'erreurs
    // sur les champs du formulaire de création et de mise à jour
    nameFieldRequired () {
      if (this.reader.name === null || this.reader.name.trim().length === 0 || this.reader.name === '') {
        return true
      } else {
        return false
      }
    },
    nameMinLength () {
      return this.errors.get('name') === 'minlength'
    },
    nameMaxLength () {
      return this.errors.get('name') === 'maxlength'
    },
    descriptionFieldRequired () {
      if (this.reader.description === null || this.reader.description.trim().length === 0 || this.reader.description === '') {
        return true
      } else {
        return false
      }
    },
    descriptionMinLength () {
      return this.errors.get('description') === 'minlength'
    },
    descriptionMaxLength () {
      return this.errors.get('description') === 'maxlength'
    },
    displayNameFieldRequired () {
      if (this.reader.displayName === null || this.reader.displayName.trim().length === 0 || this.reader.displayName === '') {
        return true
      } else {
        return false
      }
    },
    displayNameMinLength () {
      return this.errors.get('displayName') === 'minlength'
    },
    displayNameMaxLength () {
      return this.errors.get('displayName') === 'maxlength'
    },
    typesRequired () {
      if (this.reader.authorizedTypes.length < 1) {
        return true
      } else {
        return false
      }
    },
    classificationDecorationsRequired () {
      if (this.reader.classificationDecorations.length < 1) {
        return true
      } else {
        return false
      }
    },
    // Méthode en charge d'activer ou non le boutton
    // de sauvegarde du formulaire de saisie
    isAnyFieldError () {
      if (this.nameFieldRequired || this.nameMinLength || this.nameMaxLength ||
      this.displayNameFieldRequired || this.displayNameMinLength || this.displayNameMaxLength ||
      this.descriptionFieldRequired || this.descriptionMinLength || this.descriptionMaxLength ||
      this.typesRequired || this.classificationDecorationsRequired) {
        return 'disabled'
      }
      return null
    }
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets lecteurs
    loadAll () {
      ReaderService.query().then(response => {
        this.readers = response
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode permettant d'initialiser la map contenant
    // les types d'erreurs pour chaque champs de formulaire
    initMapError () {
      this.errors.set('name', null)
      this.errors.set('displayName', null)
      this.errors.set('description', null)
    },
    reset () {
      this.readers = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de lecteur
    createReader () {
      ReaderService.update(this.reader).then(() => {
        this.updateModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de lecteur
    update (id) {
      ReaderService.get(id).then(result => {
        this.reader = result
        this.updateModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de lecteur
    deleteReader (id) {
      ReaderService.get(id).then(result => {
        this.reader = result
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge de supprimer un lecteur
    confirmDelete (id) {
      ReaderService.delete(id).then(() => {
        this.deleteModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    clear () {
      this.reader = { name: null, displayName: null, description: null, id: null, authorizedTypes: [], classificationDecorations: [] }
    },
    readerDetail (readerId) {
      this.$router.push({ name: 'AdminEntityReaderDetails', params: { id: readerId } })
    },
    setError (fieldName, val, min, max) {
      if (val != null && val.length < min) {
        this.errors.set(fieldName, 'minlength')
      } else if (val != null && val.length > max) {
        this.errors.set(fieldName, 'maxlength')
      } else {
        this.errors.set(fieldName, null)
      }
    },
    containsSelectedType (type) {
      if (this.reader !== null && this.reader !== undefined) {
        const list = this.reader.authorizedTypes || []
        if (list.length > 0) {
          for (var i = 0, size = list.length; i < size; i++) {
            if (list[i] === type) {
              return true
            }
          }
        }
      }
      return false
    },
    containsSelectedDecorType (type) {
      if (this.reader !== null && this.reader !== undefined) {
        const list = this.reader.classificationDecorations || []
        if (list.length > 0) {
          for (var i = 0, size = list.length; i < size; i++) {
            if (list[i] === type) {
              return true
            }
          }
        }
      }
      return false
    },
    toggleSelectionType (type) {
      this.authorizedTypesDirty = true
      var i = 0
      var idx = -1
      for (var size = this.reader.authorizedTypes.length; i < size; i++) {
        if (this.reader.authorizedTypes[i] === type) {
          idx = i
          break
        }
      }
      // is currently selected
      if (idx > -1) {
        this.reader.authorizedTypes.splice(idx, 1)
      } else {
        // is newly selected
        this.reader.authorizedTypes.push(type)
      }
    },
    toggleSelectionDecorType (type) {
      this.classificationDecorTypesDirty = true
      var i = 0
      var idx = -1
      for (var size = this.reader.classificationDecorations.length; i < size; i++) {
        if (this.reader.classificationDecorations[i] === type) {
          idx = i
          break
        }
      }
      // is currently selected
      if (idx > -1) {
        this.reader.classificationDecorations.splice(idx, 1)
      } else {
        // is newly selected
        this.reader.classificationDecorations.push(type)
      }
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deleteReaderConfirmation)
    this.updateModal = new Modal(this.$refs.saveReaderModal)
    this.loadAll()
    this.initMapError()
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'reader.name' (newVal) {
      this.setError('name', newVal, 3, 20)
    },
    'reader.displayName' (newVal) {
      this.setError('displayName', newVal, 3, 50)
    },
    'reader.description' (newVal) {
      this.setError('description', newVal, 5, 512)
    }
  }
}
</script>
