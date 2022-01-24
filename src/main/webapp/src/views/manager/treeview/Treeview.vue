<template>
<div class="treeview">
    <h2>{{ $t("manager.treeview.title") }}</h2>
    <div class="tree-browser">
      <esup-js-tree class="default" v-if="isDataLoad" .datas="treeData" .config="treeConfig" .onSelection="onTreeSelection">
      </esup-js-tree>
    </div>
    <div class="context-viewer" ui-view="contextDetails">
    </div>
</div>
</template>

<script>
import ContextService from '@/services/entities/context/ContextService.js'

export default {
  name: 'Treeview',
  data () {
    return {
      isDataLoad: false,
      treeData: [],
      currentNode: null,
      treeConfig: {
        identifier: 'tree',
        showCheckbox: false,
        isMultipleSelection: false
      }
    }
  },
  methods: {
    // Méthode de chargement de données pour l'arborescence
    loadTreeData () {
      ContextService.query(1).then(response => {
        this.treeData = response.data
        this.treeData.forEach(element => {
          if (element && element.children) {
            element.getChildren = () => this.loadTreeDataChildren(element.id)
            element.iconIndex = this.getIconIndexByType(element.type)
          }
        })
        this.isDataLoad = true
      })
    },
    // Méthode permettant le chargement de données asynchrone pour l'arborescence
    loadTreeDataChildren (id) {
      return ContextService.query(id).then(response => {
        response.data.forEach(element => {
          if (element && element.children) {
            element.getChildren = () => this.loadTreeDataChildren(element.id)
            element.iconIndex = this.getIconIndexByType(element.type)
          }
        })
        return response.data
      })
    },
    // Méthode permettant de récupérer l'élément sélectionné dans l'arborescence
    onTreeSelection (datas) {
      this.currentNode = datas && datas.length > 0 ? datas[0] : null
      // TODO $state.go('ctxDetails', {ctxId: _l[0], ctxType: _l[1]})
    },
    // Retourne l'indice de l'icône à afficher pour un elément de l'arbre selon son type
    getIconIndexByType (type) {
      switch (type) {
        case 'ORGANIZATION':
          return 0
        case 'PUBLISHER':
          return 1
        case 'CATEGORY':
          return 2
        case 'FEED':
          return 3
        case 'ITEM':
          return 4
        default:
          return null
      }
    }
  },
  created () {
    this.loadTreeData()
  }
}
</script>
