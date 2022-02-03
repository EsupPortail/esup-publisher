<template>
  <div>
    <h2><span>{{$t('news.detail.title')}}</span> ({{$t(getItemTypeLabel(item.status))}})</h2>
    <div class="article-preview news-preview">
    <legend>
      <span>{{$t('item.preview')}}</span>
    </legend>
    <article>
      <header class="media">
        <div v-if="item.enclosure" class="media-left">
          <img class="media-object" :src="getUrlEnclosure(item.enclosure)" alt="" />
        </div>
        <div class="media-body">
          <h1 class="media-heading">{{item.title}}</h1>
          <p><span>{{$t('item.author')}}</span>: <span>{{item.createdBy.displayName}}</span>, <span>{{$t('item.publishDate')}}</span>:
            <time pubdate="pubdate">{{formatDateDayMonthYear(item.startDate)}}</time></p>
        </div>
      </header>
      <div class="details">
        <div class="summary" @click="showInfo()">
            <span v-if="open" :html="item.summary"><i class="fas fa-caret-down"></i>{{item.summary}}</span>
            <span v-if="!open" :html="item.summary"><i class="fas fa-caret-right"></i>{{item.summary}}</span>
        </div>
        <div v-if="open" class="pubInfo">
          <p>
            <span class="label">{{$t('item.createdBy')}}</span> <span>{{item.createdBy.displayName}}</span>,
            <span class="label">{{$t('item.when')}}</span> <time>{{formatDateTime(item.createdDate)}}</time>
          </p>
          <p v-if="item.lastModifiedDate">
            <span class="label">{{$t('item.lastModifiedBy')}}</span> <span>{{item.lastModifiedBy.displayName}}</span>,
            <span class="label">{{$t('item.when')}}</span> <time>{{formatDateTime(item.lastModifiedDate)}}</time>
          </p>
          <p v-if="item.validatedBy">
            <span class="label">{{$t('item.validatedBy')}}</span> <span>{{item.validatedBy.displayName}}</span>,
            <span class="label">{{$t('item.when')}}</span> <time>{{formatDateTime(item.validatedDate)}}</time>
          </p>
        </div>
      </div>
      <section v-html="item.body" class="ck-content" ></section>
    </article>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ContentDetailNews',
  inject: ['item', 'open', 'formatDateTime', 'formatDateDayMonthYear', 'getItemTypeLabel', 'getUrlEnclosure', 'showInfo']
}
</script>
