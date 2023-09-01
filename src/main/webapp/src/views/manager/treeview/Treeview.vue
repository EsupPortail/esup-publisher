<template>
  <div class="treeview">
    <h2>{{ $t('manager.treeview.title') }}</h2>
    <div class="tree-browser">
      <esup-js-tree class="default" v-if="isDataLoad" .datas="treeData" .config="treeConfig" .onSelection="onTreeSelection"> </esup-js-tree>
    </div>
    <div class="context-viewer">
      <router-view :key="$route.fullPath"></router-view>
    </div>
  </div>
</template>

<script>
import ContextService from '@/services/entities/context/ContextService.js';
import { computed, readonly } from 'vue';

export default {
  name: 'Treeview',
  data() {
    return {
      isDataLoad: false,
      treeData: [],
      parentNodeId: null,
      treeConfig: {
        identifier: 'tree',
        showCheckbox: false,
        isMultipleSelection: false,
        allowDeselection: false,
      },
    };
  },
  provide() {
    return {
      parentNodeId: readonly(computed(() => this.parentNodeId)),
      deleteTreeNode: this.deleteTreeNode,
      selectTreeNode: this.selectTreeNode,
      refreshTreeNode: this.refreshTreeNode,
    };
  },
  methods: {
    // Méthode de chargement de données pour l'arborescence
    loadTreeData() {
      ContextService.query(1).then((response) => {
        this.treeData = response.data;
        this.treeData.forEach((element) => this.initTreeNodeProperties(element));
        this.isDataLoad = true;
      });
    },
    selectTreeNode(id) {
      document.querySelector('esup-js-tree.default').selectNode(id);
    },
    deleteTreeNode(id) {
      document.querySelector('esup-js-tree.default').deleteNode(id);
    },
    refreshTreeNode(id, properties) {
      document.querySelector('esup-js-tree.default').refreshNode(id, properties, true);
    },
    // Méthode permettant le chargement de données asynchrone pour l'arborescence
    loadTreeDataChildren(id) {
      return ContextService.query(id).then((response) => {
        response.data.forEach((element) => this.initTreeNodeProperties(element));
        return response.data;
      });
    },
    // Méthode permettant de récupérer l'élément sélectionné dans l'arborescence
    onTreeSelection(datas) {
      const currentNode = datas && datas.length > 0 ? datas[0] : null;
      this.parentNodeId = currentNode && currentNode.parent ? currentNode.parent.id : null;
      if (currentNode) {
        const tmp = currentNode.id.split(':');
        this.$router.push({
          name: 'TreeviewCtxDetails',
          params: { ctxId: tmp[0], ctxType: tmp[1] },
        });
      }
    },
    // Intialisation des propriétés d'un noeud de l'arbre
    initTreeNodeProperties(node) {
      if (node.children) {
        node.getChildren = () => this.loadTreeDataChildren(node.id);
      }
      node.iconIndex = this.getIconIndexByType(node.type);
    },
    // Retourne l'indice de l'icône à afficher pour un elément de l'arbre selon son type
    getIconIndexByType(type) {
      switch (type) {
        case 'ORGANIZATION':
          return 0;
        case 'PUBLISHER':
          return 1;
        case 'CATEGORY':
          return 2;
        case 'FEED':
          return 3;
        case 'ITEM':
          return 4;
        default:
          return null;
      }
    },
  },
  created() {
    this.loadTreeData();
  },
};
</script>
