<template>
<div>
  <h2>{{ $t("audits.title") }}</h2>

  <div class="row">
    <div class="col-lg-7">
      <h4>{{ $t("audits.filter.title") }}</h4>
        <p class="input-group">
          <span class="input-group-text">{{ $t("audits.filter.from") }}</span>
          <input type="date" class="form-control form-control-sm" name="start" v-model="_fromDate" required />
          <span class="input-group-text">{{ $t("audits.filter.to") }}</span>
          <input type="date" class="form-control form-control-sm" name="end" v-model="_toDate" required />
        </p>
    </div>
  </div>

  <table class="table table-sm table-striped table-bordered table-responsive">
    <thead>
      <tr>
        <th @click="setSorting('timestamp')">{{ $t("audits.table.header.date") }}</th>
        <th @click="setSorting('principal')">{{ $t("audits.table.header.principal") }}</th>
        <th @click="setSorting('type')">{{ $t("audits.table.header.status") }}</th>
        <th @click="setSorting('data.message')">{{ $t("audits.table.header.data") }}</th>
      </tr>
    </thead>

    <tbody>
       <tr v-for="audit in filteredAudits" :key="audit.timestamp">
        <td><span>{{formatDate(audit.timestamp)}}</span></td>
        <td><small>{{audit.principal}}</small></td>
        <td>{{audit.type}}</td>
        <td>
          <span v-if="audit.data.message">{{audit.data.message}}</span>
          <span v-if="audit.data.remoteAddress">{{ $t("audits.table.data.remoteAddress") }} {{audit.data.remoteAddress}}</span>
        </td>
      </tr>
    </tbody>
  </table>
</div>
</template>

<script>
import AuditsService from '@/services/admin/AuditsService'
import DateUtils from '@/services/util/DateUtils'
import store from '@/store/index.js'

export default {
  name: 'AdminAudits',
  data () {
    return {
      // Liste des audits
      audits: [],
      // Date de début de recherche
      fromDate: null,
      // Date de fin de recherche
      toDate: null,
      // Sens du tri des audits
      reverse: false,
      // Propriété des audits sur laquelle le tri est effectué
      predicate: null
    }
  },
  computed: {
    _fromDate: {
      get () {
        return DateUtils.convertLocalDateToServer(this.fromDate)
      },
      set (newVal) {
        this.fromDate = DateUtils.convertLocalDateFromServer(newVal)
        this.onChangeDate()
      }
    },
    _toDate: {
      get () {
        return DateUtils.convertLocalDateToServer(this.toDate)
      },
      set (newVal) {
        this.toDate = DateUtils.convertLocalDateFromServer(newVal)
        this.onChangeDate()
      }
    },
    filteredAudits () {
      var filterAudits = this.audits

      // Filtre des audits
      filterAudits = filterAudits.filter(conf => !conf.filtered)

      // Tri des audits
      if (this.predicate !== null) {
        filterAudits.sort((conf1, conf2) => conf1[this.predicate].localeCompare(conf2[this.predicate]) * (this.reverse ? -1 : 1))
      }

      return filterAudits
    }
  },
  methods: {
    setSorting (predicate) {
      this.predicate = predicate
      this.reverse = !this.reverse
    },
    today () {
      // Today + 1 day - needed if the current day must be included
      var today = new Date()
      this.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1)
    },
    previousMonth () {
      var fromDate = new Date()
      if (fromDate.getMonth() === 0) {
        fromDate = new Date(fromDate.getFullYear() - 1, 0, fromDate.getDate())
      } else {
        fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate())
      }

      this.fromDate = fromDate
    },
    onChangeDate () {
      AuditsService.findByDates(this.fromDate, this.toDate).then(response => {
        this.audits = response.data
      })
    },
    formatDate (date) {
      return DateUtils.convertToIntString(DateUtils.convertDateTimeFromServer(date), {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
      }, store.getters.getLanguage)
    }
  },
  created () {
    this.today()
    this.previousMonth()
    this.onChangeDate()
  }
}
</script>
