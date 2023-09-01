import UserService from '@/services/user/UserService';

// Directive en charge de déterminer si l'utilisateur a les
// droits d'édition pour un contexte
const CanEditDirective = {
  canEdit(el, context) {
    if (context) {
      // On ne refait la requête que si les paramètres ont changé
      const keyId = context.keyId;
      const keyType = context.keyType;
      if (el.canEditKeyId !== keyId || el.canEditKeyType !== keyType) {
        el.canEditKeyId = keyId;
        el.canEditKeyType = keyType;
        el.classList.add('d-none');
        UserService.canEditCtx(keyId, keyType).then((response) => {
          if (!response.data.value) {
            el.classList.add('d-none');
          } else {
            el.classList.remove('d-none');
          }
        });
      }
    } else {
      el.canEditKeyId = undefined;
      el.canEditKeyType = undefined;
      el.classList.add('d-none');
    }
  },
  updated(el, { dir, value }) {
    dir.canEdit(el, value);
  },
  beforeMount(el, { dir, value }) {
    dir.canEdit(el, value);
  },
};

export default CanEditDirective;
