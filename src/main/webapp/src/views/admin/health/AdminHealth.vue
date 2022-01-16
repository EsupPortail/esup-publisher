<template>
<div>

    <h2>{{ $t("health.title") }}</h2>

    <div class="modal fade" id="showHealthModal" tabindex="-1" role="dialog" aria-labelledby="showHealthLabel"
         aria-hidden="true" ref="showHealthModal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form name="form" role="form" novalidate v-if="currentHealth">
                    <div class="modal-header">
                      <h4 class="modal-title" id="showHealthLabel">
                        {{ $t("health.indicator." + baseName(currentHealth.name)) }}
                        {{subSystemName(currentHealth.name)}}
                      </h4>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"
                        @click="clear()"></button>
                    </div>
                    <div class="modal-body">
                        <div v-if="currentHealth.details">
                            <h4>{{ $t("health.details.properties") }}</h4>
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th class="text-start">{{ $t("health.details.name") }}</th>
                                        <th class="text-start">{{ $t("health.details.value") }}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="(value, key) in currentHealth.details" :key="key">
                                        <td class="text-start">{{key}}</td>
                                        <td class="text-start">{{value}}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div v-if="currentHealth.error">
                            <h4>{{ $t("health.details.error") }}</h4>
                                <pre>{{currentHealth.error}}</pre>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <p>
        <button type="button" class="btn btn-primary" @click="refresh()"><span
                class="fas fa-sync"></span>&nbsp;{{ $t("health.refresh-button") }}
        </button>
    </p>

    <table id="healthCheck" class="table table-striped">
        <thead>
            <tr>
                <th class="col-lg-7">{{ $t("health.table.service") }}</th>
                <th class="col-lg-2 text-center">{{ $t("health.table.status") }}</th>
                <th class="col-lg-2 text-center">{{ $t("health.details.details") }}</th>
                <th class="col-lg-1 text-center"></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="health in healths" :key="health.name">
                <td>{{ $t("health.indicator." + baseName(health.name)) }}{{subSystemName(health.name)}}</td>
                <td class="text-center">
                    <span class="badge" :class="getLabelClass(health.status)">
                        {{ $t("health.status." + health.status) }}
                    </span>
                </td>
                <td class="text-center">
                    <a class="hand" @click="showHealth(health)" v-if="health.details || health.error">
                        <i class="fas fa-eye"></i>
                    </a>
                </td>
                <td></td>
            </tr>
        </tbody>
    </table>
</div>
</template>

<script>
import MonitoringService from '@/services/admin/MonitoringService'
import { Modal } from 'bootstrap'

export default {
  name: 'AdminHealth',
  data () {
    return {
      // Diagnostics
      healths: [],
      // Diagnostic affiché dans la modale
      currentHealth: null,
      // Modale d'affichage d'un diagnostic
      showHealthModal: null
    }
  },
  methods: {
    // Affichage de la modale d'un diagnostic
    showHealth (health) {
      this.currentHealth = health
      this.showHealthModal.show()
    },
    // Clear du diagnostic affiché dans la modale
    clear () {
      this.currentHealth = null
    },
    // Retourne la classe correspondante au statut
    // du diagnostic
    getLabelClass (statusState) {
      if (statusState === 'UP') {
        return 'bg-success'
      } else {
        return 'bg-danger'
      }
    },
    // Rafraichissement de la liste des disgnostics
    refresh () {
      MonitoringService.checkHealth().then(response => {
        this.healths = this.transformHealthData(response.data)
      }).catch(error => {
        error.text().then(text => {
          if (text) {
            this.healths = this.transformHealthData(JSON.parse(text).components)
          }
        })
      })
    },
    // Formattage des diagnostics
    transformHealthData (data) {
      var response = []
      if (data !== null && data !== undefined) {
        this.flattenHealthData(response, null, data)
      }
      return response
    },
    // Mise à plat de la liste des diagnostics
    flattenHealthData (result, path, data) {
      Object.keys(data).forEach(key => {
        const value = data[key]
        if (this.isHealthObject(value)) {
          if (this.hasSubSystem(value)) {
            this.addHealthObject(result, false, value, this.getModuleName(path, key))
            this.flattenHealthData(result, this.getModuleName(path, key), value)
          } else {
            this.addHealthObject(result, true, value, this.getModuleName(path, key))
          }
        }
      })
      return result
    },
    getModuleName (path, name) {
      var result
      if (path && name) {
        result = path + '.' + name
      } else if (path) {
        result = path
      } else if (name) {
        result = name
      } else {
        result = ''
      }
      return result
    },
    addHealthObject (result, isLeaf, healthObject, name) {
      var healthData = {
        name: name
      }
      var details = {}
      var hasDetails = false

      Object.keys(healthObject).forEach(key => {
        const value = healthObject[key]
        if (key === 'status' || key === 'error') {
          healthData[key] = value
        } else {
          if (!this.isHealthObject(value)) {
            details[key] = value
            hasDetails = true
          }
        }
      })

      // Add the of the details
      if (hasDetails) {
        Object.assign(healthData, { details: details })
      }

      // Only add nodes if they provide additional information
      if (isLeaf || hasDetails || healthData.error) {
        result.push(healthData)
      }
      return healthData
    },
    hasSubSystem (healthObject) {
      var result = false
      Object.values(healthObject).forEach(value => {
        if (value && value.status) {
          result = true
        }
      })
      return result
    },
    isHealthObject (healthObject) {
      var result = false
      Object.keys(healthObject).forEach(key => {
        if (key === 'status') {
          result = true
        }
      })
      return result
    },
    baseName (healthName) {
      if (healthName) {
        var split = healthName.split('.')
        return split[0]
      }
    },
    subSystemName (healthName) {
      if (healthName) {
        var split = healthName.split('.')
        split.splice(0, 1)
        var remainder = split.join('.')
        return remainder ? ' - ' + remainder : ''
      }
    }
  },
  created () {
    // Chargement de la liste des diagnostics
    this.refresh()
  },
  mounted () {
    // Récupération de la modale d'affichage d'un diagnostic
    this.showHealthModal = new Modal(this.$refs.showHealthModal)
  }
}
</script>
