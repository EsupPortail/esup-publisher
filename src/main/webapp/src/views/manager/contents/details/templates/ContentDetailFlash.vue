<template>
  <div>
    <h2>
      <span>{{ $t("flash.detail.title") }}</span> ({{
        $t(getItemTypeLabel(item.status))
      }})
    </h2>
    <div class="article-preview flash-preview">
      <legend>
        <span>{{ $t("item.preview") }}</span>
      </legend>
      <article>
        <header class="d-flex">
          <div v-if="item.enclosure" class="flex-shrink-0 me-3">
            <img :src="getUrlEnclosure(item.enclosure)" alt="" />
          </div>
          <div class="flex-grow-1">
            <h1 class="mt-0 mb-1">{{ item.title }}</h1>
            <p>
              <span>{{ $t("item.author") }}</span
              >: <span>{{ item.createdBy.displayName }}</span
              >, <span>{{ $t("item.publishDate") }}</span
              >:
              <time pubdate="pubdate">{{
                formatDateDayMonthYear(item.startDate)
              }}</time>
            </p>
          </div>
        </header>
        <div class="details">
          <div class="summary hand" @click="showInfo()">
            <span v-if="open" :html="item.summary"
              ><i class="fas fa-caret-down fa-lg m-1"></i
              >{{ item.summary }}</span
            >
            <span v-if="!open" :html="item.summary"
              ><i class="fas fa-caret-right fa-lg m-1"></i
              >{{ item.summary }}</span
            >
          </div>
          <div v-if="open" class="pubInfo">
            <p>
              <span class="label">{{ $t("item.createdBy") }}</span>
              <span>{{ item.createdBy.displayName }}</span
              >, <span class="label">{{ $t("item.when") }}</span>
              <time>{{ formatDateTime(item.createdDate) }}</time>
            </p>
            <p v-if="item.lastModifiedDate">
              <span class="label">{{ $t("item.lastModifiedBy") }}</span>
              <span>{{ item.lastModifiedBy.displayName }}</span
              >, <span class="label">{{ $t("item.when") }}</span>
              <time>{{ formatDateTime(item.lastModifiedDate) }}</time>
            </p>
            <p v-if="item.validatedBy">
              <span class="label">{{ $t("item.validatedBy") }}</span>
              <span>{{ item.validatedBy.displayName }}</span
              >, <span class="label">{{ $t("item.when") }}</span>
              <time>{{ formatDateTime(item.validatedDate) }}</time>
            </p>
          </div>
        </div>
        <section v-html="item.body" class="ck-content"></section>
      </article>
    </div>
  </div>
</template>
<script>
export default {
  name: "ContentDetailFlash",
  inject: [
    "item",
    "open",
    "formatDateTime",
    "formatDateDayMonthYear",
    "getItemTypeLabel",
    "getUrlEnclosure",
    "showInfo",
  ],
};
</script>
