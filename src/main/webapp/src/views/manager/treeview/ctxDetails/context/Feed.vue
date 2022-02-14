<template>
  <div v-if="context.type === 'EXTERNALFEED'">
    <ExternalFeed></ExternalFeed>
  </div>
  <div v-else-if="context.type === 'INTERNALFEED'">
    <InternalFeed></InternalFeed>
  </div>
</template>

<script>
import ExternalFeed from './ExternalFeed'
import InternalFeed from './InternalFeed'
export default {
  name: 'Feed',
  components: {
    ExternalFeed,
    InternalFeed
  },
  inject: ['context']
}
</script>
