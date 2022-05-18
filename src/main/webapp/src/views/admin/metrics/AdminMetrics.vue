<template>
  <div>
    <h2>
      <span id="metrics-page-heading" v-text="$t('metrics.title')" data-cy="metricsPageHeading"></span>
      <button class="btn btn-primary float-right" v-on:click="refresh()">
        <i class="fas fa-sync"></i>&nbsp; <span v-text="$t('metrics[\'refresh-button\']')"></span>
      </button>
    </h2>

    <h3 v-text="$t('metrics.jvm.title')"></h3>
    <div class="row" v-if="!updatingMetrics">
      <div class="col-md-4">
        <h4 v-text="$t('metrics.jvm.memory.title')"></h4>
        <div>
          <div v-for="(entry, key) of metrics.jvm" :key="key">
            <span v-if="entry.max !== -1">
              <span>{{ key }}</span> ({{ formatNumber1(entry.used / 1048576) }}M / {{ formatNumber1(entry.max / 1048576) }}M)
            </span>
            <span v-else>
              <span>{{ key }}</span> {{ formatNumber1(entry.used / 1048576) }}M
            </span>
            <div>Committed : {{ formatNumber1(entry.committed / 1048576) }}M</div>
            <div class="progress" v-if="entry.max !== -1">
              <div class="progress-bar progress-bar-striped bg-success" role="progressbar" :style="{ 'width': formatNumber((entry.used * 100) / entry.max) + '%'}"
                  aria-valuenow="{{formatNumber1((entry.used * 100) / entry.max)}}" aria-valuemin="0" aria-valuemax="100">{{formatNumber1((entry.used * 100) / entry.max) + '%'}}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <h4 v-text="$t('metrics.jvm.threads.title')"></h4>
        <span><span v-text="$t('metrics.jvm.threads.runnable')"></span> {{ threadStats.threadDumpRunnable }}</span>
        <div class="progress">
          <div :style="{ 'width': formatNumber((threadStats.threadDumpRunnable * 100) / threadStats.threadDumpAll) + '%'}" class="progress-bar progress-bar-striped bg-success" role="progressbar"
              aria-valuenow="{{formatNumber1((threadStats.threadDumpRunnable * 100) / threadStats.threadDumpAll)}}" aria-valuemin="0" aria-valuemax="{{threadStats.threadDumpAll}}">{{formatNumber1((threadStats.threadDumpRunnable * 100) / threadStats.threadDumpAll) + '%'}}</div>
        </div>

        <span><span v-text="$t('metrics.jvm.threads.timedwaiting')"></span> ({{ threadStats.threadDumpTimedWaiting }})</span>
        <div class="progress">
          <div :style="{ 'width': formatNumber((threadStats.threadDumpTimedWaiting * 100) / threadStats.threadDumpAll) + '%'}" class="progress-bar progress-bar-striped bg-warning" role="progressbar"
              aria-valuenow="{{formatNumber1((threadStats.threadDumpTimedWaiting * 100) / threadStats.threadDumpAll)}}" aria-valuemin="0" aria-valuemax="{{threadStats.threadDumpAll}}">{{formatNumber1((threadStats.threadDumpTimedWaiting * 100) / threadStats.threadDumpAll) + '%'}}</div>
        </div>

        <span><span v-text="$t('metrics.jvm.threads.waiting')"></span> ({{ threadStats.threadDumpWaiting }})</span>
        <div class="progress">
          <div :style="{ 'width': formatNumber((threadStats.threadDumpWaiting * 100) / threadStats.threadDumpAll) + '%'}" class="progress-bar progress-bar-striped bg-warning" role="progressbar"
              aria-valuenow="{{formatNumber1((threadStats.threadDumpWaiting * 100) / threadStats.threadDumpAll)}}" aria-valuemin="0" aria-valuemax="{{threadStats.threadDumpAll}}">{{formatNumber1((threadStats.threadDumpWaiting * 100) / threadStats.threadDumpAll) + '%'}}</div>
        </div>

        <span><span v-text="$t('metrics.jvm.threads.blocked')"></span> ({{ threadStats.threadDumpBlocked }})</span>
        <div class="progress">
          <div :style="{ 'width': formatNumber((threadStats.threadDumpBlocked * 100) / threadStats.threadDumpAll) + '%' }" class="progress-bar progress-bar-striped bg-danger" role="progressbar"
              aria-valuenow="{{formatNumber1((threadStats.threadDumpBlocked * 100) / threadStats.threadDumpAll)}}" aria-valuemin="0" aria-valuemax="{{threadStats.threadDumpAll}}">{{formatNumber1((threadStats.threadDumpBlocked * 100) / threadStats.threadDumpAll) + '%'}}</div>
        </div>

        <span
          >Total: {{ threadStats.threadDumpAll }}
          <a class="hand" href="#" v-on:click="showModal()" data-target="#threadDump">
            <i class="fas fa-eye"></i>
          </a>
        </span>
      </div>
      <div class="col-md-4">
        <h4>System</h4>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-4">Uptime</div>
          <div class="col-md-8 text-right">{{ convertMillisecondsToDuration(metrics.processMetrics['process.uptime']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-4">Start time</div>
          <div class="col-md-8 text-right">{{ convertTimestampToMillis(metrics.processMetrics['process.start.time']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">Process CPU usage</div>
          <div class="col-md-3 text-right">{{ formatNumber2(100 * metrics.processMetrics['process.cpu.usage']) }} %</div>
        </div>
        <div class="progress">
          <div :style="{ 'width': formatNumber(100 * metrics.processMetrics['process.cpu.usage']) + '%' }" class="progress-bar progress-bar-striped bg-success" role="progressbar"
              aria-valuenow="{{formatNumber(100 * metrics.processMetrics['process.cpu.usage'])}}" aria-valuemin="0" aria-valuemax="100">{{formatNumber(100 * metrics.processMetrics['process.cpu.usage']) + '%'}}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">System CPU usage</div>
          <div class="col-md-3 text-right">{{ formatNumber2(100 * metrics.processMetrics['system.cpu.usage']) }} %</div>
        </div>
        <div class="progress">
          <div :style="{ 'width': formatNumber(100 * metrics.processMetrics['system.cpu.usage']) + '%' }" class="progress-bar progress-bar-striped bg-success" role="progressbar"
              aria-valuenow="{{formatNumber(100 * metrics.processMetrics['system.cpu.usage'])}}" aria-valuemin="0" aria-valuemax="100">{{formatNumber(100 * metrics.processMetrics['system.cpu.usage']) + '%'}}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">System CPU count</div>
          <div class="col-md-3 text-right">{{ metrics.processMetrics['system.cpu.count'] }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">System 1m Load average</div>
          <div class="col-md-3 text-right">{{ formatNumber2(metrics.processMetrics['system.load.average.1m']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">Process files max</div>
          <div class="col-md-3 text-right">{{ formatNumber1(metrics.processMetrics['process.files.max']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">Process files open</div>
          <div class="col-md-3 text-right">{{ formatNumber1(metrics.processMetrics['process.files.open']) }}</div>
        </div>
      </div>
    </div>

    <h3 v-text="$t('metrics.jvm.gc.title')"></h3>
    <div class="row" v-if="!updatingMetrics && isObjectExisting(metrics, 'garbageCollector')">
      <div class="col-md-4">
        <div>
          <span>
            GC Live Data Size/GC Max Data Size ({{ formatNumber1(metrics.garbageCollector['jvm.gc.live.data.size'] / 1048576) }}M /
            {{ formatNumber1(metrics.garbageCollector['jvm.gc.max.data.size'] / 1048576) }}M)
          </span>
          <div class="progress">
            <div :style="{ 'width': formatNumber((100 * metrics.garbageCollector['jvm.gc.live.data.size']) / metrics.garbageCollector['jvm.gc.max.data.size']) + '%' }" class="progress-bar progress-bar-striped bg-success" role="progressbar"
                aria-valuenow="{{metrics.garbageCollector['jvm.gc.live.data.size']}}" aria-valuemin="0" aria-valuemax="{{metrics.garbageCollector['jvm.gc.max.data.size']}}">{{ metrics.garbageCollector['jvm.gc.live.data.size'] }}</div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div>
          <span>
            GC Memory Promoted/GC Memory Allocated ({{ formatNumber1(metrics.garbageCollector['jvm.gc.memory.promoted'] / 1048576) }}M /
            {{ formatNumber1(metrics.garbageCollector['jvm.gc.memory.allocated'] / 1048576) }}M)
          </span>
          <div class="progress">
            <div :style="{ 'width': formatNumber((100 * metrics.garbageCollector['jvm.gc.memory.promoted']) / metrics.garbageCollector['jvm.gc.memory.allocated']) + '%' }" class="progress-bar progress-bar-striped bg-success" role="progressbar"
                aria-valuenow="{{metrics.garbageCollector['jvm.gc.memory.promoted']}}" aria-valuemin="0" aria-valuemax="{{metrics.garbageCollector['jvm.gc.memory.allocated']}}">{{ metrics.garbageCollector['jvm.gc.memory.promoted'] }}</div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="row">
          <div class="col-md-9">Classes loaded</div>
          <div class="col-md-3 text-right">{{ metrics.garbageCollector.classesLoaded }}</div>
        </div>
        <div class="row">
          <div class="col-md-9">Classes unloaded</div>
          <div class="col-md-3 text-right">{{ metrics.garbageCollector.classesUnloaded }}</div>
        </div>
      </div>
      <div class="table-responsive">
        <table class="table table-striped" aria-describedby="Jvm gc">
          <thead>
            <tr>
              <th scope="col"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.count')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.mean')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.min')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p50')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p75')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p95')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p99')"></th>
              <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.max')"></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>jvm.gc.pause</td>
              <td class="text-right">{{ metrics.garbageCollector['jvm.gc.pause'].count }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause'].mean) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.0']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.5']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.75']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.95']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.99']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause'].max) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <h3 v-text="$t('metrics.jvm.http.title')"></h3>
    <table
      class="table table-striped"
      v-if="!updatingMetrics && isObjectExisting(metrics, 'http.server.requests')"
      aria-describedby="Jvm http"
    >
      <thead>
        <tr>
          <th scope="col" v-text="$t('metrics.jvm.http.table.code')"></th>
          <th scope="col" v-text="$t('metrics.jvm.http.table.count')"></th>
          <th scope="col" class="text-right" v-text="$t('metrics.jvm.http.table.mean')"></th>
          <th scope="col" class="text-right" v-text="$t('metrics.jvm.http.table.max')"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(entry, key) of metrics['http.server.requests']['percode']" :key="key">
          <td>{{ key }}</td>
          <td>
          <div class="progress">
            <div :style="{ 'width': formatNumber((100 * entry.count) / metrics['http.server.requests']['all'].count) + '%' }" class="progress-bar progress-bar-striped bg-animated bg-success" role="progressbar"
                aria-valuenow="{{entry.count}}" aria-valuemin="0" aria-valuemax="{{metrics['http.server.requests']['all'].count}}">{{ formatNumber1(entry.count) }}</div>
          </div>
          </td>
          <td class="text-right">
            {{ formatNumber2(filterNaN(entry.mean)) }}
          </td>
          <td class="text-right">{{ formatNumber2(entry.max) }}</td>
        </tr>
      </tbody>
    </table>

    <h3>Endpoints requests (time in millisecond)</h3>
    <div class="table-responsive" v-if="!updatingMetrics">
      <table class="table table-striped" aria-describedby="Endpoint">
        <thead>
          <tr>
            <th scope="col">Method</th>
            <th scope="col">Endpoint url</th>
            <th scope="col" class="text-right">Count</th>
            <th scope="col" class="text-right">Mean</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="(entry, entryKey) of metrics.services">
            <tr v-for="(method, methodKey) of entry" :key="entryKey + '-' + methodKey">
              <td>{{ methodKey }}</td>
              <td>{{ entryKey }}</td>
              <td class="text-right">{{ method.count }}</td>
              <td class="text-right">{{ formatNumber2(method.mean) }}</td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <h3 v-text="$t('metrics.cache.title')"></h3>
    <div class="table-responsive" v-if="!updatingMetrics && isObjectExisting(metrics, 'cache')">
      <table class="table table-striped" aria-describedby="Cache">
        <thead>
          <tr>
            <th scope="col" v-text="$t('metrics.cache.cachename')"></th>
            <th scope="col" class="text-right" data-translate="metrics.cache.hits">Cache Hits</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.misses">Cache Misses</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.gets">Cache Gets</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.puts">Cache Puts</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.removals">Cache Removals</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.evictions">Cache Evictions</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.hitPercent">Cache Hit %</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.missPercent">Cache Miss %</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entry, key) of metrics.cache" :key="key">
            <td>{{ key }}</td>
            <td class="text-right">{{ entry['cache.gets.hit'] }}</td>
            <td class="text-right">{{ entry['cache.gets.miss'] }}</td>
            <td class="text-right">{{ entry['cache.gets.hit'] + entry['cache.gets.miss'] }}</td>
            <td class="text-right">{{ entry['cache.puts'] }}</td>
            <td class="text-right">{{ entry['cache.removals'] }}</td>
            <td class="text-right">{{ entry['cache.evictions'] }}</td>
            <td class="text-right">
              {{ formatNumber2(filterNaN((100 * entry['cache.gets.hit']) / (entry['cache.gets.hit'] + entry['cache.gets.miss']))) }}
            </td>
            <td class="text-right">
              {{ formatNumber2(filterNaN((100 * entry['cache.gets.miss']) / (entry['cache.gets.hit'] + entry['cache.gets.miss']))) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <h3 v-text="$t('metrics.datasource.title')"></h3>
    <div class="table-responsive" v-if="!updatingMetrics && isObjectExistingAndNotEmpty(metrics, 'databases')">
      <table class="table table-striped" aria-describedby="Connection pool">
        <thead>
          <tr>
            <th scope="col">
              <span v-text="$t('metrics.datasource.usage')"></span> (active: {{ metrics.databases.active.value }}, min:
              {{ metrics.databases.min.value }}, max: {{ metrics.databases.max.value }}, idle: {{ metrics.databases.idle.value }})
            </th>
            <th scope="col" class="text-right" v-text="$t('metrics.datasource.count')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.datasource.mean')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.min')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p50')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p75')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p95')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.servicesstats.table.p99')"></th>
            <th scope="col" class="text-right" v-text="$t('metrics.datasource.max')"></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Acquire</td>
            <td class="text-right">{{ metrics.databases.acquire.count }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.acquire.mean)) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.0']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.5']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.75']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.95']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.99']) }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.acquire.max)) }}</td>
          </tr>
          <tr>
            <td>Creation</td>
            <td class="text-right">{{ metrics.databases.creation.count }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.creation.mean)) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.0']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.5']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.75']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.95']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.99']) }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.creation.max)) }}</td>
          </tr>
          <tr>
            <td>Usage</td>
            <td class="text-right">{{ metrics.databases.usage.count }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.usage.mean)) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.0']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.5']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.75']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.95']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.99']) }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.usage.max)) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div id="threadDump" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" ref="metricsModal">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title" id="showMetricsLabel" v-text="$t('metrics.jvm.threads.dump.title')"></h4>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <span class="badge bg-primary" v-on:click="threadStateFilter = ''"
              >All&nbsp;<span class="badge badge-pill badge-default">{{ threadData.threadDumpAll }}</span
              >&nbsp;<span class="badge rounded-pill bg-secondary">{{threadStats.threadDumpAll}}</span></span
            >&nbsp;
            <span class="badge bg-success" v-on:click="threadStateFilter = 'RUNNABLE'"
              >Runnable&nbsp;<span class="badge badge-pill badge-default">{{ threadData.threadDumpRunnable }}</span
              >&nbsp;<span class="badge rounded-pill bg-secondary">{{threadStats.threadDumpRunnable}}</span></span
            >&nbsp;
            <span class="badge bg-info" v-on:click="threadStateFilter = 'WAITING'"
              >Waiting&nbsp;<span class="badge badge-pill badge-default">{{ threadData.threadDumpWaiting }}</span
              >&nbsp;<span class="badge rounded-pill bg-secondary">{{threadStats.threadDumpWaiting}}</span></span
            >&nbsp;
            <span class="badge bg-warning" v-on:click="threadStateFilter = 'TIMED_WAITING'"
              >Timed Waiting&nbsp;<span class="badge badge-pill badge-default">{{ threadData.threadDumpTimedWaiting }}</span
              >&nbsp;<span class="badge rounded-pill bg-secondary">{{threadStats.threadDumpTimedWaiting}}</span></span
            >&nbsp;
            <span class="badge bg-danger" v-on:click="threadStateFilter = 'BLOCKED'"
              >Blocked&nbsp;<span class="badge badge-pill badge-default">{{ threadData.threadDumpBlocked }}</span
              >&nbsp;<span class="badge rounded-pill bg-secondary">{{threadStats.threadDumpBlocked}}</span></span
            >&nbsp;
            <div class="mt-2">&nbsp;</div>
            Filter
            <!-- <input type="text" v-model="threadStateFilter" class="form-control" /> -->
            <div class="pad" v-for="(entry, key) of filteredThreadDumps" :key="key">
              <h6>
                <span class="badge" :class="getLabelClass(entry.threadState)">{{ entry.threadState }}</span
                >&nbsp;{{ entry.threadName }} (ID {{ entry.threadId }})
                <a v-on:click="entry.show = !entry.show" href="javascript:void(0);">
                  <span :hidden="entry.show" v-text="$t('metrics.jvm.threads.dump.show')"></span>
                  <span :hidden="!entry.show" v-text="$t('metrics.jvm.threads.dump.hide')"></span>
                </a>
              </h6>
              <div class="card" :hidden="!entry.show">
                <div class="card-body">
                  <div v-for="(st, key) of entry.stackTrace" :key="key" class="break">
                    <samp
                      >{{ st.className }}.{{ st.methodName }}(<code>{{ st.fileName }}:{{ st.lineNumber }}</code
                      >)</samp
                    >
                    <span class="mt-1"></span>
                  </div>
                </div>
              </div>
              <table class="table table-sm table-responsive" aria-describedby="Metrics">
                <thead>
                  <tr>
                    <th v-text="$t('metrics.jvm.threads.dump.blockedtime')" scope="col"></th>
                    <th v-text="$t('metrics.jvm.threads.dump.blockedcount')" scope="col"></th>
                    <th v-text="$t('metrics.jvm.threads.dump.waitedtime')" scope="col"></th>
                    <th v-text="$t('metrics.jvm.threads.dump.waitedcount')" scope="col"></th>
                    <th v-text="$t('metrics.jvm.threads.dump.lockname')" scope="col"></th>
                    <th v-text="$t('metrics.jvm.threads.dump.stacktrace')" scope="col"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>{{ entry.blockedTime }}</td>
                    <td>{{ entry.blockedCount }}</td>
                    <td>{{ entry.waitedTime }}</td>
                    <td>{{ entry.waitedCount }}</td>
                    <td class="thread-dump-modal-lock" :title="entry.lockName">
                      <code>{{ entry.lockName }}</code>
                    </td>
                    <td>
                        <a tabindex="0" role="button" data-bs-toggle="popover" data-bs-placement="left" @click="entry.showPopover = !entry.showPopover">
                            <span v-if="!entry.showPopover">{{ $t("metrics.jvm.threads.dump.show") }}</span>
                            <span v-if="entry.showPopover">{{ $t("metrics.jvm.threads.dump.hide") }}</span>
                        </a>
                        <div class="popover" v-if="entry.showPopover">
                            <div class="popover-header">
                              <h4>{{ $t("metrics.jvm.threads.dump.stacktrace") }}<button type="button" class="btn-close float-end" @click="entry.showPopover = !entry.showPopover"></button></h4>
                            </div>
                            <div class="popover-body">
                                <div v-for="st in entry.stackTrace" :key="st.className + '.' + st.methodName + '.' + st.fileName + '.' + st.lineNumber">
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
            <button type="button" class="btn btn-default btn-outline-dar" data-bs-dismiss="modal">Close</button>
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
      threadData: [],
      // Stats des threadDump
      threadStats: {
        // Nombre de thread dumps à l'état Runnable
        threadDumpRunnable: 0,
        // Nombre de thread dumps à l'état Waiting
        threadDumpWaiting: 0,
        // Nombre de thread dumps à l'état TimedWaiting
        threadDumpTimedWaiting: 0,
        // Nombre de thread dumps à l'état Blocked
        threadDumpBlocked: 0,
        // Nombre total de thread dumps
        threadDumpAll: 0
      },
      // Modale d'affichage des thread dumpp
      showThreadDumpsModal: null,
      // Filtre des threadDump
      threadStateFilter: ''
    }
  },
  computed: {
    filteredThreadDumps () {
      var filteredThreadDumps = this.threadData

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
    formatNumber1 (val) {
      return this.formatNumber(val, 1)
    },
    formatNumber2 (val) {
      return this.formatNumber(val, 2)
    },
    filterNaN (input) {
      if (isNaN(input)) {
        return 0
      }
      return input
    },
    convertMillisecondsToDuration (ms) {
      const times = {
        year: 31557600000,
        month: 2629746000,
        day: 86400000,
        hour: 3600000,
        minute: 60000,
        second: 1000
      }
      let timeString = ''
      let plural = ''
      for (const key in times) {
        if (Math.floor(ms / times[key]) > 0) {
          if (Math.floor(ms / times[key]) > 1) {
            plural = 's'
          } else {
            plural = ''
          }
          timeString += `${Math.floor(ms / times[key])} ${key}${plural} `
          ms = ms - times[key] * Math.floor(ms / times[key])
        }
      }
      return timeString
    },
    convertTimestampToMillis (timestamp) {
      return new Date(timestamp).toString()
    },
    refresh () {
      this.updatingMetrics = true
      MonitoringService.getMetrics().then(response => {
        this.metrics = response.data
        this.loadStats()
        this.updatingMetrics = false
        this.refreshThreadDumpData()
      }).catch(error => {
        error.text().then(text => {
          if (text) {
            this.metrics = JSON.parse(text)
            this.loadStats()
          }
          this.updatingMetrics = true
        })
      })
    },
    refreshThreadDumpData () {
      MonitoringService.threadDump().then(response => {
        this.threadData = response.data.threads

        this.threadStats = {
          threadDumpRunnable: 0,
          threadDumpWaiting: 0,
          threadDumpTimedWaiting: 0,
          threadDumpBlocked: 0,
          threadDumpAll: 0
        }

        this.threadData.forEach(value => {
          value.showPopover = false
          if (value.threadState === 'RUNNABLE') {
            this.threadStats.threadDumpRunnable += 1
          } else if (value.threadState === 'WAITING') {
            this.threadStats.threadDumpWaiting += 1
          } else if (value.threadState === 'TIMED_WAITING') {
            this.threadStats.threadDumpTimedWaiting += 1
          } else if (value.threadState === 'BLOCKED') {
            this.threadStats.threadDumpBlocked += 1
          }
        })
        this.threadStats.threadDumpAll = this.threadStats.threadDumpRunnable + this.threadStats.threadDumpWaiting + this.threadStats.threadDumpTimedWaiting + this.threadStats.threadDumpBlocked
      }).catch(() => {
        this.updatingMetrics = true
      })
    },
    loadStats () {
      this.servicesStats = {}
      this.cachesStats = {}

      Object.keys(this.metrics.services).forEach(key => {
        if (key.indexOf('/api/') !== -1) {
          this.servicesStats[key] = this.metrics.services[key]
        }
      })
      Object.keys(this.metrics.cache).forEach(key => {
        var index = key.lastIndexOf('.')
        var newKey = key.substring(0, index)
        this.cachesStats[newKey] = {
          name: key,
          value: this.metrics.cache[key]
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
    },
    isObjectExisting (metrics, key) {
      return metrics && metrics[key]
    },
    isObjectExistingAndNotEmpty (metrics, key) {
      return this.isObjectExisting(metrics, key) && JSON.stringify(metrics[key]) !== '{}'
    }
  },
  created () {
    // Chargement des métriques
    this.refresh()
  },
  mounted () {
    // Récupération de la modale d'affichage des thred dumps
    this.showThreadDumpsModal = new Modal(this.$refs.metricsModal)
  }
}
</script>
