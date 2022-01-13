<template>
<div class='filter'>
    <h2 >{{$t('filter.home.title')}}</h2>
    <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#saveFilterModal" @click="clear" v-has-any-role="'ROLE_ADMIN'">
        <span class="fas fa-bolt"></span> <span >{{$t('filter.home.createLabel')}}</span>
    </button>
    <div class="modal fade" id="saveFilterModal" tabindex="-1" role="dialog" aria-labelledby="myFilterLabel"
         aria-hidden="true" ref="saveFilterModal">
        <div class="modal-dialog modal-fullscreen-md-down modal-lg">
            <div class="modal-content">
                <form name="editForm" role="form" novalidate show-validation>
                  <div class="modal-header">
                        <h4 class="modal-title" id="myFilterLabel">{{$t('filter.home.createOrEditLabel')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                      <div class="modal-body">
                        <div class="form-group">
                            <label class="control-label" for="ID" >ID</label>
                            <input type="text" class="form-control" name="id" id="ID"
                                   v-model="filter.id" disabled>
                        </div>
                        <div class="form-group">
                          <label for ="type" class="control-label">{{$t('filter.type')}}</label>
                          <select class="form-select"  id="type" v-model="filter.type">
                            <option v-for="type in filterTypeList" :key="type.id" :value="type">{{type}}</option>
                          </select>
                        </div>
                        <div class="form-group">
                            <label for ="pattern" class="control-label">{{$t('filter.pattern')}}</label>
                            <input type="text" class="form-control" :class="(patternMinLength || patternMaxLength) ? 'is-invalid' : 'valid'" name="pattern" id="pattern"
                                   v-model="filter.pattern" required>
                            <div>
                                <p class="help-block"
                                   v-if="patternFieldRequired">
                                    {{$t('entity.validation.required')}}
                                </p>
                                <p class="help-block"
                                   v-if="patternMinLength">
                                    {{$t('entity.validation.minlength', {min: '3'})}}
                                </p>
                                <p class="help-block"
                                   v-if="patternMaxLength" >
                                    {{$t('entity.validation.maxlength', {max:'512'})}}
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="description" class="control-label">{{$t('filter.description')}}</label>
                            <input type="text" class="form-control"  name="description" id="description"
                                   v-model="filter.description">
                        </div>
                          <div>
                          <label  class="control-label">{{$t('filter.organization')}}</label>
                          <select class="form-select" name="organization"  id="organization" v-model="filter.organization">
                            <option v-for="organization in organizations" :key="organization.id " :value="organization">{{organization.name}}</option>
                          </select>
                        </div>
                    </div>
                         <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-primary" :class="isAnyFieldError" @click="createFilter" >
                            <span class="fas fa-download"></span>&nbsp;<span>{{$t('entity.action.save')}}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="modal fade" ref="deleteFilterConfirmation">
        <div class="modal-dialog">
            <div class="modal-content">
                <form name="deleteForm">
                    <div class="modal-header">
                        <h4 class="modal-title">{{$t('entity.delete.title')}}</h4>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                                @click="clear"></button>
                    </div>
                    <div class="modal-body">
                        <p>{{$t('filter.delete.question', {id: filter.id})}}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{$t('entity.action.cancel')}}</span>
                        </button>
                        <button type="button" class="btn btn-danger" @click="confirmDelete(filter.id)">
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
                    <th>{{$t('filter.pattern')}}</th>
                    <th>{{$t('filter.description')}}</th>
                    <th>{{$t('filter.type')}}</th>
                    <th>{{$t('filter.organization')}}</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="filter in filters" :key="filter.id">
                    <td><router-link :to="{ name: 'AdminEntityFilterDetails', params: { id: filter.id }}">
                    {{filter.id}}</router-link></td>
                    <td>{{filter.pattern}}</td>
                    <td>{{filter.description}}</td>
                    <td>{{filter.type}}</td>
                    <td>{{filter.organization.name}}</td>
                    <td>
                        <button type="submit"
                                @click="filterDetail(filter.id)"
                                class="btn btn-info btn-sm me-1">
                            <span class="far fa-eye"></span>&nbsp;<span>{{$t("entity.action.view")}}</span>
                        </button>
                        <button type="submit"
                                @click="update(filter.id)"
                                class="btn btn-primary btn-sm me-1" v-has-any-role="'ROLE_ADMIN'">
                            <span class="fas fa-pencil-alt"></span>&nbsp;<span>{{$t("entity.action.edit")}}</span>
                        </button>
                        <button type="submit"
                                @click="deleteFilter(filter.id)"
                                class="btn btn-danger btn-sm" v-has-any-role="'ROLE_ADMIN'">
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
import FilterService from '@/services/entities/filter/FilterService'
import OrganizationService from '@/services/entities/organization/OrganizationService'
import EnumDatasService from '@/services/entities/enum/EnumDatasService'
import { Modal } from 'bootstrap'

export default {
  name: 'Filter',
  data () {
    return {
      filters: [],
      organizations: [],
      filter: { pattern: null, description: null, type: null, id: null, organization: null },
      deleteModal: null,
      updateModal: null,
      errors: new Map()
    }
  },
  computed: {
    filterTypeList () {
      return EnumDatasService.getFilterTypeList()
    },
    // Méthodes en charge du lancement des messages d'erreurs
    // sur les champs du formulaire de création et de mise à jour
    patternFieldRequired () {
      if (this.filter.pattern === null || this.filter.pattern.trim().length === 0 || this.filter.pattern === '') {
        return true
      } else {
        return false
      }
    },
    patternMinLength () {
      return this.errors.get('pattern') === 'minlength'
    },
    patternMaxLength () {
      return this.errors.get('pattern') === 'maxlength'
    },
    // Méthode en charge d'activer ou non le boutton
    // de sauvegarde du formulaire de saisie
    isAnyFieldError () {
      if (this.patternFieldRequired || this.patternMaxLength || this.patternMinLength) {
        return 'disabled'
      }
      return null
    }
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets filtres
    loadAll () {
      FilterService.query().then(response => {
        this.filters = response
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode permettant d'initialiser la map contenant
    // les types d'erreurs pour chaque champs de formulaire
    initMapError () {
      this.errors.set('pattern', null)
    },
    reset () {
      this.filters = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de filtre
    createFilter () {
      FilterService.update(this.filter).then(() => {
        this.updateModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de filtre
    update (id) {
      FilterService.get(id).then(result => {
        this.filter = result
        this.updateModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de filtre
    deleteFilter (id) {
      FilterService.get(id).then(result => {
        this.filter = result
        this.deleteModal.show()
      }).catch(error => {
        console.error(error)
      })
    },
    // Méthode en charge de supprimer un filtre
    confirmDelete (id) {
      FilterService.delete(id).then(() => {
        this.deleteModal.hide()
        this.reset()
        this.clear()
      }).catch(error => {
        console.error(error)
      })
    },
    clear () {
      this.filter = { pattern: null, description: null, type: this.filterTypeList[0], id: null, organization: this.organizations[0] }
    },
    filterDetail (filterId) {
      this.$router.push({ name: 'AdminEntityFilterDetails', params: { id: filterId } })
    },
    setError (fieldName, val, min, max) {
      if (val != null && val.length < min) {
        this.errors.set(fieldName, 'minlength')
      } else if (val != null && val.length > max) {
        this.errors.set(fieldName, 'maxlength')
      } else {
        this.errors.set(fieldName, null)
      }
    }
  },
  mounted () {
    this.deleteModal = new Modal(this.$refs.deleteFilterConfirmation)
    this.updateModal = new Modal(this.$refs.saveFilterModal)
    this.loadAll()
    this.initMapError()
  },
  created () {
    OrganizationService.query().then(response => {
      this.organizations = response
    }).catch(error => {
      console.error(error)
    })
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'filter.pattern' (newVal) {
      this.setError('pattern', newVal, 3, 255)
    }
  }
}
</script>
