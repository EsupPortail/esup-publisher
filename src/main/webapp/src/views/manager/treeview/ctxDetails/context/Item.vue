<template>
  <div v-if="context.type === 'NEWS'">
    <News></News>
  </div>
  <div v-else-if="context.type === 'MEDIA'">
    <Media></Media>
  </div>
  <div v-else-if="context.type === 'RESOURCE'">
    <Resource></Resource>
  </div>
  <div v-else-if="context.type === 'FLASH'">
    <Flash></Flash>
  </div>
  <div v-else>
    <Empty></Empty>
  </div>
</template>

<script>
import News from "./News";
import Media from "./Media";
import Resource from "./Resource";
import Flash from "./Flash";
import Empty from "./Empty";

export default {
  name: "TabItem",
  components: {
    News,
    Media,
    Resource,
    Flash,
    Empty,
  },
  inject: ["context"],
};
</script>
