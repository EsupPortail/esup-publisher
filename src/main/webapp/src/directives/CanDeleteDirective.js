import UserService from "@/services/user/UserService";

// Directive en charge de déterminer si l'utilisateur a les
// droits de suppression pour un contexte
const CanDeleteDirective = {
  canDelete(el, context) {
    if (context) {
      // On ne refait la requête que si les paramètres ont changé
      const keyId = context.keyId;
      const keyType = context.keyType;
      if (el.canDeleteKeyId !== keyId || el.canDeleteKeyType !== keyType) {
        el.canDeleteKeyId = keyId;
        el.canDeleteKeyType = keyType;
        el.classList.add("d-none");
        UserService.canDeleteCtx(keyId, keyType).then((response) => {
          if (!response.data.value) {
            el.classList.add("d-none");
          } else {
            el.classList.remove("d-none");
          }
        });
      }
    } else {
      el.canDeleteKeyId = undefined;
      el.canDeleteKeyType = undefined;
      el.classList.add("d-none");
    }
  },
  updated(el, { dir, value }) {
    dir.canDelete(el, value);
  },
  beforeMount(el, { dir, value }) {
    dir.canDelete(el, value);
  },
};

export default CanDeleteDirective;
