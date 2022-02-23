import { shallowMount } from '@vue/test-utils'
import Treeview from '@/views/manager/treeview/Treeview'
import ContextService from '@/services/entities/context/ContextService.js'

// Tests unitaires sur la page Treeview
describe('Treeview.vue tests', () => {
  let wrapper
  let $router
  let datas

  beforeEach(() => {
    datas = [{
      id: '1:PUBLISHER',
      type: 'PUBLISHER',
      children: true,
      parent: {
        id: '1:ORGANIZATION'
      }
    }]
    ContextService.query = jest.fn().mockReturnValue(Promise.resolve({
      data: datas
    }))
    const $t = (param) => param
    const $route = {
      fullPath: 'fullPath'
    }
    $router = {
      push: jest.fn()
    }
    wrapper = shallowMount(Treeview, {
      global: {
        stubs: {
          EsupJsTree: { template: '<div class="esup-js-tree-stub"></div>' },
          RouterView: { template: '<div class="router-view-stub"></div>' }
        },
        mocks: {
          $t,
          $route,
          $router
        }
      }
    })
  })

  it('test 1 Treeview - Initialisation', done => {
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find('.esup-js-tree-stub').exists()).toBe(true)
      expect(wrapper.vm.treeData.length).toStrictEqual(1)
      expect(wrapper.vm.treeData[0].id).toStrictEqual(datas[0].id)
      expect(wrapper.vm.treeData[0].getChildren).toBeDefined()
      expect(wrapper.vm.treeData[0].iconIndex).toStrictEqual(1)
      expect(wrapper.vm.isDataLoad).toStrictEqual(true)
      expect(ContextService.query).toHaveBeenCalledTimes(1)
      expect(ContextService.query).toHaveBeenCalledWith(1)
      done()
    })
  })

  it('test 2 Treeview - onTreeSelection', done => {
    wrapper.vm.$nextTick(() => {
      wrapper.vm.onTreeSelection(datas)
      expect(wrapper.vm.parentNodeId).toStrictEqual(datas[0].parent.id)
      expect($router.push).toHaveBeenCalledTimes(1)
      expect($router.push).toHaveBeenCalledWith({ name: 'TreeviewCtxDetails', params: { ctxId: '1', ctxType: 'PUBLISHER' } })
      done()
    })
  })

  it('test 3 Treeview - getIconIndexByType', done => {
    wrapper.vm.$nextTick(() => {
      expect(wrapper.vm.getIconIndexByType('ORGANIZATION')).toStrictEqual(0)
      expect(wrapper.vm.getIconIndexByType('PUBLISHER')).toStrictEqual(1)
      expect(wrapper.vm.getIconIndexByType('CATEGORY')).toStrictEqual(2)
      expect(wrapper.vm.getIconIndexByType('FEED')).toStrictEqual(3)
      expect(wrapper.vm.getIconIndexByType('ITEM')).toStrictEqual(4)
      expect(wrapper.vm.getIconIndexByType('test')).toStrictEqual(null)
      done()
    })
  })
})
