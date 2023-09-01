import UserService from '@/services/user/UserService';

// Directive en charge de déterminer si l'utilisateur a les
// droits d'édition sur les cibles pour un contexte
const CanEditTargetsDirective = {
  canEditCtxTargets(el, context) {
    if (context) {
      // On ne refait la requête que si les paramètres ont changé
      const keyId = context.keyId;
      const keyType = context.keyType;
      if (el.canEditTargetsKeyId !== keyId || el.canEditTargetsKeyType !== keyType) {
        el.canEditTargetsKeyId = keyId;
        el.canEditTargetsKeyType = keyType;
        el.classList.add('d-none');
        UserService.canEditCtxTargets(keyId, keyType).then((response) => {
          if (!response.data.value) {
            el.classList.add('d-none');
          } else {
            el.classList.remove('d-none');
          }
        });
      }
    } else {
      el.canEditTargetsKeyId = undefined;
      el.canEditTargetsKeyType = undefined;
      el.classList.add('d-none');
    }
  },
  updated(el, { dir, value }) {
    dir.canEditCtxTargets(el, value);
  },
  beforeMount(el, { dir, value }) {
    dir.canEditCtxTargets(el, value);
  },
};

export default CanEditTargetsDirective;
