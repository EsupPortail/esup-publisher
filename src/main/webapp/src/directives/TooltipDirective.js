import { Tooltip } from 'bootstrap'

// Directive permettant d'afficher un tooltip
// Utilisation :
// <button v-tooltip="foo">Hover me</button>
// <button v-tooltip.click="bar">Click me</button>
// <button v-tooltip.html="baz">Html</button>
// <button v-tooltip:top="foo">Top</button>
// <button v-tooltip:left="foo">Left</button>
// <button v-tooltip:right="foo">Right</button>
// <button v-tooltip:bottom="foo">Bottom</button>
// <button v-tooltip:auto="foo">Auto</button>
// <button v-tooltip:auto.html="clock" @click="clock = Date.now()">Updating</button>
const TooltipDirective = {
  createToolTip (el, binding) {
    let trigger
    if (binding.modifiers.focus || binding.modifiers.hover || binding.modifiers.click) {
      const t = []
      if (binding.modifiers.focus) t.push('focus')
      if (binding.modifiers.hover) t.push('hover')
      if (binding.modifiers.click) t.push('click')
      trigger = t.join(' ')
    }

    /* eslint-disable-next-line no-new */
    new Tooltip(el, {
      title: binding.value || '',
      placement: binding.arg || 'top',
      trigger: trigger || 'hover',
      html: binding.modifiers.html || false
    })
  },
  disposeToolTip (el) {
    Tooltip.getInstance(el).dispose()
  },
  unmounted (el, binding) {
    binding.dir.disposeToolTip(el)
  },
  updated (el, binding) {
    binding.dir.disposeToolTip(el)
    binding.dir.createToolTip(el, binding)
  },
  mounted (el, binding) {
    binding.dir.createToolTip(el, binding)
  }
}

export default TooltipDirective
