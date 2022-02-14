<template>
  <div v-if="context.type === 'ORGANIZATION'">
    <Organization></Organization>
  </div>
  <div v-else-if="context.type === 'PUBLISHER'">
    <Publisher></Publisher>
  </div>
  <div v-else-if="context.type === 'CATEGORY'">
    <Category></Category>
  </div>
  <div v-else-if="context.type === 'FEED'">
    <Feed></Feed>
  </div>
  <div v-else-if="context.type === 'ITEM'">
    <Item></Item>
  </div>
  <div v-else>
    <Empty></Empty>
  </div>
</template>

<script>
import Organization from './Organization'
import Publisher from './Publisher'
import Empty from './Empty'
import Category from './Category'
import Feed from './Feed'
import Item from './Item'

export default {
  name: 'TabContext',
  components: {
    Organization,
    Publisher,
    Category,
    Feed,
    Item,
    Empty
  },
  data () {
    return {
      context: { type: this.$route.params.ctxType, id: this.$route.params.ctxId }
    }
  },
  provide () {
    return {
      deleteNodeAndRefresh: this.deleteNodeAndRefresh,
      detailSubject: this.detailSubject,
      getUrlEnclosure: this.getUrlEnclosure
    }
  },
  methods: {
    // Méthode de redirection vers la page de détail d'une publication
    detailSubject (id) {
      this.$router.push({ name: 'ContentDetail', params: { id: id } })
    },
    // Url de fichier (local ou distant)
    getUrlEnclosure (enclosure) {
      if (enclosure) {
        return enclosure.startsWith('https:') || enclosure.startsWith('http:') || enclosure.startsWith('ftp:') ? enclosure : process.env.VUE_APP_BACK_BASE_URL + enclosure
      }
      return null
    }
  }
}
</script>
