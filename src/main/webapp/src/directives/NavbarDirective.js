import store from "@/store/index.js";

// Directive en charge de mettre en surbrillance la langue utilisée dans
// la barre de navigation des pages d'administration
const NavbarDirective = {
  updateClass(el, value) {
    var language = value;
    if (language === store.getters.getLanguage) {
      el.classList.add("active");
    } else {
      el.classList.remove("active");
    }
  },
  updated(el, { dir, value }) {
    dir.updateClass(el, value);
  },
  beforeMount(el, { dir, value }) {
    dir.updateClass(el, value);
  },
};

export default NavbarDirective;
