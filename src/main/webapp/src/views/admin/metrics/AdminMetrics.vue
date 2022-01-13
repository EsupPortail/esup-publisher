<template>
<div>
  <h2>{{ $t("metrics.title") }}</h2>
  <p>
    <button type="button" class="btn btn-primary" @click="refresh()"><span class="fas fa-sync"></span>&nbsp;{{ $t("metrics.refresh-button") }}</button>
  </p>

  <div class="card" v-if="updatingMetrics">{{ $t("metrics.updating") }}</div>

  <h3 v-if="!updatingMetrics && metrics !== null">{{  $t("metrics.jvm.title") }}</h3>
  <div class="row" v-if="!updatingMetrics && metrics !== null">
      <div class="col-lg-4">
          <b>{{  $t("metrics.jvm.memory.title") }}</b>
          <p>{{ $t("metrics.jvm.memory.total") }} ({{formatNumber(metrics.gauges['jvm.memory.total.used'].value / 1000000)}}M / {{formatNumber(metrics.gauges['jvm.memory.total.max'].value / 1000000)}}M)</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-success" role="progressbar"
                  aria-valuenow="{{formatNumber(metrics.gauges['jvm.memory.total.used'].value / 1000000)}}"
                  aria-valuemin="0"
                  aria-valuemax="{{formatNumber(metrics.gauges['jvm.memory.total.max'].value / 1000000)}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.memory.total.used'].value * 100 / metrics.gauges['jvm.memory.total.max'].value) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.memory.total.used'].value * 100 / metrics.gauges['jvm.memory.total.max'].value)}}%
              </div>
          </div>
          <p>{{ $t("metrics.jvm.memory.heap") }} ({{formatNumber(metrics.gauges['jvm.memory.heap.used'].value / 1000000)}}M / {{formatNumber(metrics.gauges['jvm.memory.heap.max'].value / 1000000)}}M)</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-success" role="progressbar"
                  aria-valuenow="{{formatNumber(metrics.gauges['jvm.memory.heap.used'].value / 1000000)}}"
                  aria-valuemin="0"
                  aria-valuemax="{{formatNumber(metrics.gauges['jvm.memory.heap.max'].value / 1000000)}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.memory.heap.usage'].value * 100) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.memory.heap.usage'].value * 100)}}%
              </div>
          </div>
          <p>{{ $t("metrics.jvm.memory.nonheap") }} ({{formatNumber(metrics.gauges['jvm.memory.non-heap.used'].value / 1000000)}}M / {{formatNumber(metrics.gauges['jvm.memory.non-heap.committed'].value / 1000000)}}M)</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-success" role="progressbar"
                  aria-valuenow="{{formatNumber(metrics.gauges['jvm.memory.non-heap.used'].value / 1000000)}}"
                  aria-valuemin="0"
                  aria-valuemax="{{formatNumber(metrics.gauges['jvm.memory.non-heap.committed'].value / 1000000)}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.memory.non-heap.used'].value * 100 / metrics.gauges['jvm.memory.non-heap.committed'].value) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.memory.non-heap.used'].value * 100 / metrics.gauges['jvm.memory.non-heap.committed'].value)}}%
              </div>
          </div>
      </div>
      <div class="col-lg-4">
          <b>{{ $t("metrics.jvm.threads.title") }}</b> (Total: {{metrics.gauges['jvm.threads.count'].value}}) <a class="hand" @click="showModal()"><i class="fas fa-eye"></i></a>
          <p>{{ $t("metrics.jvm.threads.runnable") }} {{metrics.gauges['jvm.threads.runnable.count'].value}}</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-success" role="progressbar"
                  aria-valuenow="{{metrics.gauges['jvm.threads.runnable.count'].value}}"
                  aria-valuemin="0"
                  aria-valuemax="{{metrics.gauges['jvm.threads.count'].value}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.threads.runnable.count'].value * 100 / metrics.gauges['jvm.threads.count'].value) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.threads.runnable.count'].value * 100 / metrics.gauges['jvm.threads.count'].value)}}%
              </div>
          </div>
          <p>{{ $t("metrics.jvm.threads.timedwaiting") }} ({{metrics.gauges['jvm.threads.timed_waiting.count'].value}})</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-warning" role="progressbar"
                  aria-valuenow="{{metrics.gauges['jvm.threads.timed_waiting.count'].value}}"
                  aria-valuemin="0"
                  aria-valuemax="{{metrics.gauges['jvm.threads.count'].value}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.threads.timed_waiting.count'].value * 100 / metrics.gauges['jvm.threads.count'].value) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.threads.timed_waiting.count'].value * 100 / metrics.gauges['jvm.threads.count'].value)}}%
              </div>
          </div>
          <p>{{ $t("metrics.jvm.threads.waiting") }} ({{metrics.gauges['jvm.threads.waiting.count'].value}})</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-warning" role="progressbar"
                  aria-valuenow="{{metrics.gauges['jvm.threads.waiting.count'].value}}"
                  aria-valuemin="0"
                  aria-valuemax="{{metrics.gauges['jvm.threads.count'].value}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.threads.waiting.count'].value * 100 / metrics.gauges['jvm.threads.count'].value) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.threads.waiting.count'].value * 100 / metrics.gauges['jvm.threads.count'].value)}}%
              </div>
          </div>
          <p>{{ $t("metrics.jvm.threads.blocked") }}  ({{metrics.gauges['jvm.threads.blocked.count'].value}})</p>
          <div class="progress mb-3">
              <div class="progress-bar progress-bar-striped bg-danger" role="progressbar"
                  aria-valuenow="{{metrics.gauges['jvm.threads.blocked.count'].value}}"
                  aria-valuemin="0"
                  aria-valuemax="{{metrics.gauges['jvm.threads.count'].value}}"
                  :style="{width: formatNumber(metrics.gauges['jvm.threads.blocked.count'].value * 100 / metrics.gauges['jvm.threads.count'].value) + '%'}">
                  {{formatNumber(metrics.gauges['jvm.threads.blocked.count'].value * 100 / metrics.gauges['jvm.threads.count'].value)}}%
              </div>
          </div>
      </div>
      <div class="col-lg-4">
          <b>{{ $t("metrics.jvm.gc.title") }}</b>
          <div class="row">
              <div class="col-lg-9">{{ $t("metrics.jvm.gc.oldgenerationcount") }}</div>
              <div class="col-lg-3 text-end">{{(metrics.gauges['jvm.garbage.G1-Old-Generation.count'] || {}).value}}</div>
          </div>
          <div class="row">
              <div class="col-lg-9">{{ $t("metrics.jvm.gc.oldgenerationtime") }}</div>
              <div class="col-lg-3 text-end">{{(metrics.gauges['jvm.garbage.G1-Old-Generation.time'] || {}).value}}ms</div>
          </div>
          <div class="row">
              <div class="col-lg-9">{{ $t("metrics.jvm.gc.younggenerationcount") }}</div>
              <div class="col-lg-3 text-end">{{(metrics.gauges['jvm.garbage.G1-Young-Generation.count'] || {}).value}}</div>
          </div>
          <div class="row">
              <div class="col-lg-9">{{ $t("metrics.jvm.gc.younggenerationtime") }}</div>
              <div class="col-lg-3 text-end">{{(metrics.gauges['jvm.garbage.G1-Young-Generation.time'] || {}).value}}ms</div>
          </div>
      </div>
  </div>

  <h3 v-if="!updatingMetrics && metrics !== null" class="mt-3">{{ $t("metrics.jvm.http.title") }}</h3>
  <p v-if="!updatingMetrics && metrics !== null">{{ $t("metrics.jvm.http.active") }} <b>{{formatNumber(metrics.counters['com.codahale.metrics.servlet.InstrumentedFilter.activeRequests'].count)}}</b> - {{ $t("metrics.jvm.http.total") }} <b>{{formatNumber(metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count)}}</b></p>
  <div v-if="!updatingMetrics && metrics !== null" class="table-responsive">
    <table class="table table-striped">
      <thead>
        <tr>
          <th>{{ $t("metrics.jvm.http.table.code") }}</th>
          <th>{{ $t("metrics.jvm.http.table.count") }}</th>
          <th class="text-end">{{ $t("metrics.jvm.http.table.mean") }}</th>
          <th class="text-end">{{ $t("metrics.jvm.http.table.average") }} (1 min)</th>
          <th class="text-end">{{ $t("metrics.jvm.http.table.average") }} (5 min)</th>
          <th class="text-end">{{ $t("metrics.jvm.http.table.average") }} (15 min)</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>{{ $t("metrics.jvm.http.code.ok") }}</td>
          <td>
              <div class="progress">
                <div class="progress-bar progress-bar-striped bg-success" role="progressbar"
                    aria-valuenow="{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].count * 100 / metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count}}"
                    aria-valuemin="0"
                    aria-valuemax="{{metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count}}"
                    :style="{width: formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].count * 100 / metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count) + '%'}">
                    {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].count}}
                </div>
              </div>
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].mean_rate, 2)}}
          </td>
          <td class="text-end">{{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m1_rate, 2)}}
          </td>
          <td class="text-end">{{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m5_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m15_rate, 2)}}
          </td>
        </tr>
        <tr>
          <td>{{ $t("metrics.jvm.http.code.notfound") }}</td>
          <td>
              <div class="progress">
                  <div class="progress-bar progress-bar-striped bg-warning" role="progressbar"
                      aria-valuenow="{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].count * 100 / metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count}}"
                      aria-valuemin="0"
                      aria-valuemax="{{metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count}}"
                      :style="{width: formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].count * 100 / metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count) + '%'}">
                      {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].count}}
                  </div>
              </div>
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].mean_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m1_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m5_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m15_rate, 2)}}
          </td>
        </tr>
        <tr>
          <td>{{ $t("metrics.jvm.http.code.servererror") }}</td>
          <td>
              <div class="progress">
                  <div class="progress-bar progress-bar-striped bg-danger" role="progressbar"
                      aria-valuenow="{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].count * 100 / metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count}}"
                      aria-valuemin="0"
                      aria-valuemax="{{metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count}}"
                      :style="{width: formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].count * 100 / metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count) + '%'}">
                      {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].count}}
                  </div>
              </div>
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].mean_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m1_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m5_rate, 2)}}
          </td>
          <td class="text-end">
              {{formatNumber(metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m15_rate, 2)}}
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <h3 v-if="!updatingMetrics && metrics !== null" class="mt-3">{{ $t("metrics.servicesstats.title") }}</h3>
  <div v-if="!updatingMetrics && metrics !== null" class="table-responsive">
    <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t("metrics.servicesstats.table.name") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.count") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.mean") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.min") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.p50") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.p75") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.p95") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.p99") }}</th>
            <th class="text-end">{{ $t("metrics.servicesstats.table.max") }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(value, key) in servicesStats" :key="key">
            <td>{{key}}</td>
            <td class="text-end">{{value.count}}</td>
            <td class="text-end">{{formatNumber(value.mean * 1000)}}</td>
            <td class="text-end">{{formatNumber(value.min * 1000)}}</td>
            <td class="text-end">{{formatNumber(value.p50 * 1000)}}</td>
            <td class="text-end">{{formatNumber(value.p75 * 1000)}}</td>
            <td class="text-end">{{formatNumber(value.p95 * 1000)}}</td>
            <td class="text-end">{{formatNumber(value.p99 * 1000)}}</td>
            <td class="text-end">{{formatNumber(value.max * 1000)}}</td>
          </tr>
        </tbody>
    </table>
  </div>

  <h3 v-if="!updatingMetrics && metrics !== null" class="mt-3">{{ $t("metrics.cache.title") }}</h3>
  <div v-if="!updatingMetrics && metrics !== null" class="table-responsive">
    <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t("metrics.cache.cachename") }}</th>
            <th class="text-end">{{ $t("metrics.cache.hits") }}</th>
            <th class="text-end">{{ $t("metrics.cache.misses") }}</th>
            <th class="text-end">{{ $t("metrics.cache.gets") }}</th>
            <th class="text-end">{{ $t("metrics.cache.puts") }}</th>
            <th class="text-end">{{ $t("metrics.cache.removals") }}</th>
            <th class="text-end">{{ $t("metrics.cache.evictions") }}</th>
            <th class="text-end">{{ $t("metrics.cache.hitPercent") }}</th>
            <th class="text-end">{{ $t("metrics.cache.missPercent") }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(value, key) in cachesStats" :key="key">
            <td>{{value.name}}</td>
            <td class="text-end">{{metrics.gauges[key + '.cache-hits'].value}}</td>
            <td class="text-end">{{metrics.gauges[key + '.cache-misses'].value}}</td>
            <td class="text-end">{{metrics.gauges[key + '.cache-hits'].value + metrics.gauges[key + '.cache-misses'].value}}</td>
            <td class="text-end">{{metrics.gauges[key + '.cache-puts'].value}}</td>
            <td class="text-end">{{metrics.gauges[key + '.cache-removals'].value}}</td>
            <td class="text-end">{{metrics.gauges[key + '.cache-evictions'].value}}</td>
            <td class="text-end">{{formatNumber(metrics.gauges[key + '.cache-hit-percentage'].value, 2)}}</td>
            <td class="text-end">{{formatNumber(metrics.gauges[key + '.cache-miss-percentage'].value, 2)}}</td>
          </tr>
        </tbody>
    </table>
  </div>

  <h3 v-if="!updatingMetrics && metrics !== null && (metrics.gauges['HikariPool-0.pool.TotalConnections'] || {}).value > 0" class="mt-3">{{ $t("metrics.datasource.title") }}</h3>
  <div v-if="!updatingMetrics && metrics !== null && (metrics.gauges['HikariPool-0.pool.TotalConnections'] || {}).value > 0" class="table-responsive">
    <table class="table table-striped">
      <thead>
        <tr>
          <th>{{ $t("metrics.datasource.usage") }} ({{metrics.gauges['HikariPool-0.pool.ActiveConnections'].value}} / {{metrics.gauges['HikariPool-0.pool.TotalConnections'].value}})</th>
          <th class="text-end">{{ $t("metrics.datasource.count") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.mean") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.min") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.p50") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.p75") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.p95") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.p99") }}</th>
          <th class="text-end">{{ $t("metrics.datasource.max") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>
            <div class="progress">
              <div class="progress-bar progress-bar-striped bg-success" role="progressbar"
                  aria-valuenow="{{formatNumber(metrics.gauges['HikariPool-0.pool.ActiveConnections'].value)}}"
                  aria-valuemin="0"
                  aria-valuemax="{{formatNumber(metrics.gauges['HikariPool-0.pool.TotalConnections'].value)}}"
                  :style="{width: formatNumber(metrics.gauges['HikariPool-0.pool.ActiveConnections'].value * 100 / metrics.gauges['HikariPool-0.pool.TotalConnections'].value) + '%'}">
                  {{formatNumber(metrics.gauges['HikariPool-0.pool.ActiveConnections'].value * 100 / metrics.gauges['HikariPool-0.pool.TotalConnections'].value)}}%
              </div>
            </div>
          </td>
          <td class="text-end">{{metrics.histograms['HikariPool-0.pool.Usage'].count}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].mean, 2)}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].min, 2)}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].p50, 2)}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].p75, 2)}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].p95, 2)}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].p99, 2)}}</td>
          <td class="text-end">{{formatNumber(metrics.histograms['HikariPool-0.pool.Usage'].max, 2)}}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <div id="threadDump" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" ref="showThreadDumpsModal">
      <div class="modal-dialog modal-lg">
          <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">{{ $t("metrics.jvm.threads.dump.title") }}</h4>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-hidden="true"></button>
              </div>
              <div class="modal-body">
                  <span class="badge bg-primary" @click="threadStateFilter = null">{{ $t("metrics.jvm.threads.all") }}&nbsp;<span class="badge rounded-pill bg-secondary">{{countThreadDumpRunnable + countThreadDumpWaiting + countThreadDumpTimedWaiting + countThreadDumpBlocked}}</span></span>&nbsp;
                  <span class="badge bg-success" @click="threadStateFilter = 'RUNNABLE'">{{ $t("metrics.jvm.threads.runnable") }}&nbsp;<span class="badge rounded-pill bg-secondary">{{countThreadDumpRunnable}}</span></span>&nbsp;
                  <span class="badge bg-info" @click="threadStateFilter = 'WAITING'">{{ $t("metrics.jvm.threads.waiting") }}&nbsp;<span class="badge rounded-pill bg-secondary">{{countThreadDumpWaiting}}</span></span>&nbsp;
                  <span class="badge bg-warning" @click="threadStateFilter = 'TIMED_WAITING'">{{ $t("metrics.jvm.threads.timedwaiting") }}&nbsp;<span class="badge rounded-pill bg-secondary">{{countThreadDumpTimedWaiting}}</span></span>&nbsp;
                  <span class="badge bg-danger" @click="threadStateFilter = 'BLOCKED'">{{ $t("metrics.jvm.threads.blocked") }}&nbsp;<span class="badge rounded-pill bg-secondary">{{countThreadDumpBlocked}}</span></span>&nbsp;
                  <div class="voffset2">&nbsp;</div>
                  <div class="row" v-for="threadDump in filteredThreadDumps" :key="threadDump.threadId">
                      <h5><span class="badge" :class="getLabelClass(threadDump.threadState)">&nbsp;</span>&nbsp;{{threadDump.threadName}} ({{ $t("metrics.jvm.threads.dump.id") }} {{threadDump.threadId}})</h5>
                      <table class="table table-sm">
                          <thead>
                            <tr>
                                <th class="text-end">{{ $t("metrics.jvm.threads.dump.blockedtime") }}</th>
                                <th class="text-end">{{ $t("metrics.jvm.threads.dump.blockedcount") }}</th>
                                <th class="text-end">{{ $t("metrics.jvm.threads.dump.waitedtime") }}</th>
                                <th class="text-end">{{ $t("metrics.jvm.threads.dump.waitedcount") }}</th>
                                <th>{{ $t("metrics.jvm.threads.dump.lockname") }}</th>
                                <th>{{ $t("metrics.jvm.threads.dump.stacktrace") }}</th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr>
                                <td>{{threadDump.blockedTime}}</td>
                                <td>{{threadDump.blockedCount}}</td>
                                <td>{{threadDump.waitedTime}}</td>
                                <td>{{threadDump.waitedCount}}</td>
                                <td>{{threadDump.lockName}}</td>
                                <td>
                                    <a tabindex="0" role="button" data-bs-toggle="popover" data-bs-placement="left" @click="threadDump.showPopover = !threadDump.showPopover">
                                        <span v-if="!threadDump.showPopover">{{ $t("metrics.jvm.threads.dump.show") }}</span>
                                        <span v-if="threadDump.showPopover">{{ $t("metrics.jvm.threads.dump.hide") }}</span>
                                    </a>
                                    <div class="popover" v-if="threadDump.showPopover">
                                        <div class="popover-header">
                                          <h4>{{ $t("metrics.jvm.threads.dump.stacktrace") }}<button type="button" class="btn-close float-end" @click="threadDump.showPopover = !threadDump.showPopover"></button></h4>
                                        </div>
                                        <div class="popover-body">
                                            <div v-for="st in threadDump.stackTrace" :key="st.className + '.' + st.methodName + '.' + st.fileName + '.' + st.lineNumber">
                                                {{st.className}}.{{st.methodName}}({{st.fileName}}:{{st.lineNumber}})
                                                <span class="voffset1"></span>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                          </tbody>
                      </table>
                  </div>
              </div>
              <div class="modal-footer">
                  <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">Close</button>
              </div>
          </div>
      </div>
  </div>
</div>
</template>

<script>
import MonitoringService from '@/services/admin/MonitoringService'
import { Modal } from 'bootstrap'
import store from '@/store/index.js'

export default {
  name: 'AdminMetrics',
  data () {
    return {
      // Métriques
      metrics: null,
      // Indique si les métriques sont en cours de chargement
      updatingMetrics: true,
      // Statistiques des services
      servicesStats: {},
      // Statistiques des caches
      cachesStats: {},
      // Liste des thread dumps
      threadDumps: [],
      // Nombre de thread dumps à l'état Runnable
      countThreadDumpRunnable: 0,
      // Nombre de thread dumps à l'état Waiting
      countThreadDumpWaiting: 0,
      // Nombre de thread dumps à l'état TimedWaiting
      countThreadDumpTimedWaiting: 0,
      // Nombre de thread dumps à l'état Blocked
      countThreadDumpBlocked: 0,
      // Modale d'affichage des thread dumpp
      showThreadDumpsModal: null,
      // Filtre sur l'état des thread dumpp
      threadStateFilter: null
    }
  },
  computed: {
    filteredThreadDumps () {
      var filteredThreadDumps = this.threadDumps

      // Filtre des loggers
      if (this.threadStateFilter !== null && this.threadStateFilter !== '') {
        filteredThreadDumps = filteredThreadDumps.filter(td => td.threadState === this.threadStateFilter)
      }

      return filteredThreadDumps
    }
  },
  methods: {
    formatNumber (val, nbDigits = 0) {
      return val.toLocaleString(store.getters.getLanguage, {
        minimumFractionDigits: nbDigits,
        maximumFractionDigits: nbDigits
      })
    },
    refresh () {
      this.updatingMetrics = true
      MonitoringService.getMetrics().then(res => {
        this.metrics = res
        this.loadStats()
        this.updatingMetrics = false
      }).catch(error => {
        error.text().then(text => {
          if (text) {
            this.metrics = JSON.parse(text)
            this.loadStats()
          }
          this.updatingMetrics = false
        })
      })
    },
    refreshThreadDumpData () {
      MonitoringService.threadDump().then(res => {
        this.threadDumps = res.threads

        this.countThreadDumpRunnable = 0
        this.countThreadDumpWaiting = 0
        this.countThreadDumpTimedWaiting = 0
        this.countThreadDumpBlocked = 0

        this.threadDumps.forEach(value => {
          value.showPopover = false
          if (value.threadState === 'RUNNABLE') {
            this.countThreadDumpRunnable += 1
          } else if (value.threadState === 'WAITING') {
            this.countThreadDumpWaiting += 1
          } else if (value.threadState === 'TIMED_WAITING') {
            this.countThreadDumpTimedWaiting += 1
          } else if (value.threadState === 'BLOCKED') {
            this.countThreadDumpBlocked += 1
          }
        })
      })
    },
    loadStats () {
      this.servicesStats = {}
      this.cachesStats = {}

      Object.keys(this.metrics.timers).forEach(key => {
        if (key.indexOf('web.rest') !== -1 || key.indexOf('service') !== -1) {
          this.servicesStats[key] = this.metrics.timers[key]
        }

        if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
          // remove gets or puts
          var index = key.lastIndexOf('.')
          var newKey = key.substr(0, index)

          // Keep the name of the domain
          index = newKey.lastIndexOf('.')
          this.cachesStats[newKey] = {
            name: newKey.substr(index + 1),
            value: this.metrics.timers[key]
          }
        }
      })
      Object.keys(this.metrics.gauges).forEach(key => {
        if (key.indexOf('jcache.statistics') !== -1) {
          // Clean prefix name and gets or puts, etc...
          var index = key.lastIndexOf('.')
          var readableKey = key.substring('jcache.statistics.'.length, index)

          // Keep the class name
          index = key.lastIndexOf('.')
          var newKey = key.substr(0, index)
          this.cachesStats[newKey] = {
            name: readableKey,
            value: this.metrics.gauges[key]
          }
        }
      })
    },
    getLabelClass (threadState) {
      if (threadState === 'RUNNABLE') {
        return 'bg-success'
      } else if (threadState === 'WAITING') {
        return 'bg-info'
      } else if (threadState === 'TIMED_WAITING') {
        return 'bg-warning'
      } else if (threadState === 'BLOCKED') {
        return 'bg-danger'
      }
    },
    showModal () {
      this.refreshThreadDumpData()
      this.showThreadDumpsModal.show()
    }
  },
  created () {
    // Chargement des métriques
    this.refresh()
  },
  mounted () {
    // Récupération de la modale d'affichage des thred dumps
    this.showThreadDumpsModal = new Modal(this.$refs.showThreadDumpsModal)
  }
}
</script>
