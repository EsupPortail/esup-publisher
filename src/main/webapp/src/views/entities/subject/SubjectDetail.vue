<template>
    <div class="modal fade" id="detailSubjectModal" ref="detailSubjectModal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div v-if="keyType === 'PERSON'">
                    <div class="modal-header">
                        <h2 class="modal-title"><span>{{$t('subject.user.detail-title')}}</span></h2>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                            @click="close"></button>
                    </div>
                    <div class="modal-body">
                        <dl class="row entity-details">
                            <dt class="col-sm-5">
                                <span>{{$t('subject.user.id')}}</span>
                            </dt>
                            <dd class="col-sm-7">
                                <span class="subject-key">{{subject.modelId.keyId}}</span>
                            </dd>
                        </dl>
                        <dl class="row entity-details">
                            <dt class="col-sm-5">
                                <span>{{$t('subject.user.displayName')}}</span>
                            </dt>
                            <dd class="col-sm-7">
                                <span>{{subject.displayName}}</span>
                            </dd>
                        </dl>
                        <template v-for="attribute in attributes" :key="attribute">
                            <dl class="row entity-details">
                                <dt class="col-sm-5">
                                    <span>{{$t('subject.user.' + attribute)}}</span>
                                </dt>
                                <dd class="col-sm-7">
                                    <span v-for="value in subject.attributes[attribute]" :key="value.id" class="list-comma">{{value}}</span>
                                </dd>
                            </dl>
                        </template>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" @click="close" data-bs-dismiss="modal">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{ $t("entity.action.close") }}</span>
                        </button>
                    </div>
                </div>
                <div v-else-if="keyType === 'GROUP'">
                    <div class="modal-header">
                        <h2 class="modal-title"><span>{{$t('subject.group.detail-title')}}</span></h2>
                        <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal"
                            @click="close"></button>
                    </div>
                    <div class="modal-body">
                        <dl class="row entity-details">
                            <dt class="col-sm-5">
                                <span>{{$t('subject.group.id')}}</span>
                            </dt>
                            <dd class="col-sm-7">
                                <span class="subject-key">{{subject.modelId.keyId}}</span>
                            </dd>
                        </dl>
                        <dl class="row entity-details">
                            <dt class="col-sm-5">
                                <span>{{$t('subject.group.displayName')}}</span>
                            </dt>
                            <dd class="col-sm-7">
                                <span>{{subject.displayName}}</span>
                            </dd>
                        </dl>
                        <template v-for="attribute in attributes" :key="attribute">
                            <dl class="row entity-details">
                                <dt class="col-sm-5">
                                    <span>{{$t('subject.group.' + attribute)}}</span>
                                </dt>
                                <dd class="col-sm-7">
                                    <span v-for="value in subject.attributes[attribute]" :key="value.id" class="list-comma">{{value}}</span>
                                </dd>
                            </dl>
                        </template>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-outline-dark" @click="close" data-bs-dismiss="modal">
                            <span class="fas fa-ban"></span>&nbsp;<span>{{ $t("entity.action.close") }}</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { Modal } from 'bootstrap'
import SubjectService from '@/services/params/SubjectService'

export default {
  name: 'SubjectDetail',
  data () {
    return {
      keyType: null,
      subject: { modelId: { keyId: '' }, displayName: '' },
      attributes: [],
      detailSubjectModal: null
    }
  },
  methods: {
    // Initialisation des données et affichage de la modale de détail
    showSubjectModal (target) {
      SubjectService.getSubjectInfos(target.keyType, target.keyId).then(response => {
        this.subject = response.data
        this.attributes = target.keyType === 'PERSON' ? SubjectService.getUserDisplayedAttrs() : SubjectService.getGroupDisplayedAttrs()
        this.keyType = target.keyType
        this.detailSubjectModal.show()
      })
    },
    // Fermeture et nettoyage des objects
    close () {
      this.detailSubjectModal.hide()
      this.subject = { modelId: { keyId: '' }, displayName: '' }
      this.attributes = {}
    }
  },
  mounted () {
    this.detailSubjectModal = new Modal(this.$refs.detailSubjectModal)
  }
}
</script>
