// Directive permettant de désactiver le clic sur un élément
const DisableClickDirective = {
  disableClick(el, disable) {
    if (el.tagName === 'A') {
      el.style['pointer-events'] = disable ? 'none' : 'auto';
    } else {
      if (disable) {
        el.setAttribute('disabled', disable);
      } else {
        el.removeAttribute('disabled');
      }
    }
  },
  updated(el, { dir, value }) {
    dir.disableClick(el, value);
  },
  beforeMount(el, { dir, value }) {
    dir.disableClick(el, value);
  },
};

export default DisableClickDirective;
