import UserService from '@/services/user/UserService';

// Directive en charge de déterminer si l'utilisateur a les
// droits de modération
const CanModerateDirective = {
  canModerate(el) {
    el.classList.add('d-none');
    UserService.canModerateAnyThing().then((response) => {
      if (!response.data.value) {
        el.classList.add('d-none');
      } else {
        el.classList.remove('d-none');
      }
    });
  },
  beforeMount(el, { dir }) {
    dir.canModerate(el);
  },
};

export default CanModerateDirective;
