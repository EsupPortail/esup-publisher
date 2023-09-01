# Migration du front-end vers Vue

A la suite de la migration du front-end de AngularJS vers Vue 3, celui-ci est maintenant accessible depuis le contexte /publisher/ui.
Le font-end fonctionne sur la version LTS NodeJS 16.

Les commandes NPM pour manipuler le front-end sont (à exécuter à la racine du projet) :

-   `npm install` : Installation des dépendances du front-end.
-   `npm run serve` : Permet de lancer le front-end en mode développement (équivalent du `grunt serve`). Le front est alors accessible à l'adresse http://localhost:3000/publisher/ui/
-   `npm run build` : Permet de construire les fichiers statiques du front-end pour la production (équivalent du `grunt build`). Les sources compilées sont générées dans le répertoire src/main/webapp/dist.
-   `npm run lint` : Permet de lancer une analyse eslint sur le code source du front-end.
-   `npm run test:unit` : Permet de lancer les tests unitaires du front-end. Ces tests se trouvent dans le répertoire src/test/javascript/spec.
-   `npm run format:check` : Permet de lancer le check du formattage avec prettier
-   `npm run format` : Réalise le formattage avec prettier

Les commandes liées au back-end restent inchangées (à exécuter à la racine du projet) :

-   Lancement en local : `./mvnw clean spring-boot:run -Dmaven.test.skip=true -Pdev`
-   Construction du livrable : `./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"` (le front-end est construit par Maven via le plugin frontend-maven-plugin, cf. pom.xml).

## Correspondances AngularJS - Vue

| Brique                       | AngularJS                                                         | Vue                                                                  |
| ---------------------------- | ----------------------------------------------------------------- | -------------------------------------------------------------------- |
| Configuration Npm            | package.json                                                      | package.json                                                         |
| Tests unitaires              | src\test\javascript\                                              | src\test\javascript                                                  |
| Router                       | src\main\webapp\scripts\app\app.js                                | src\main\webapp\src\router\index.js                                  |
| Internationalisation         | src\main\webapp\scripts\app\app.js                                | src\main\webapp\src\i18n\index.js                                    |
| Navbar                       | src\main\webapp\scripts\components\navbar\navbar.controller.js    | src\main\webapp\src\components\navbar\NavBar.vue                     |
| Spinner                      | src\main\webapp\scripts\components\interceptors\loadingSpinner.js | src\main\webapp\src\components\spinner\Spinner.vue                   |
| Page Home                    | src\main\webapp\scripts\app\main\main.js                          | src\main\webapp\src\views\Home.vue                                   |
| Page Login                   | src\main\webapp\scripts\app\account\login\login.js                | src\main\webapp\src\views\account\login\Login.vue                    |
| Page Accès refusé            | src\main\webapp\scripts\app\error\error.js                        | src\main\webapp\src\views\error\AccessDenied.vue                     |
| Page Publier                 | src\main\webapp\scripts\app\manager\publish\publish.js            | src\main\webapp\src\views\manager\publish\Publish.vue                |
| Page API                     | src\main\webapp\scripts\app\admin\docs\docs.js                    | src\main\webapp\src\views\admin\docs\AdminDocs.vue                   |
| Page Logs                    | src\main\webapp\scripts\app\admin\logs\logs.js                    | src\main\webapp\src\views\admin\logs\AdminLogs.vue                   |
| Page Metrics                 | src\main\webapp\scripts\app\admin\metrics\metrics.js              | src\main\webapp\src\views\admin\metrics\AdminMetrics.vue             |
| Page Configuration           | src\main\webapp\scripts\app\admin\configuration\configuration.js  | src\main\webapp\src\views\admin\configuration\AdminConfiguration.vue |
| Page Diagnostics             | src\main\webapp\scripts\app\admin\health\health.js                | src\main\webapp\src\views\admin\heath\AdminHealth.vue                |
| Page Audits                  | src\main\webapp\scripts\app\admin\audits\audits.js                | src\main\webapp\src\views\admin\audits\AdminAudits.vue               |
| Page Structure               | src\main\webapp\scripts\app\entities\organization\organization.js | src\main\webapp\src\views\entities\organization\Organization.vue     |
| Page Filtre                  | src\main\webapp\scripts\app\entities\filter\filter.js             | src\main\webapp\src\views\entities\filter\Filter.vue                 |
| Page Lecteur                 | src\main\webapp\scripts\app\entities\reader\reader.js             | src\main\webapp\src\views\entities\reader\Reader.vue                 |
| Page Redacteur               | src\main\webapp\scripts\app\entities\redactor\redactor.js         | src\main\webapp\src\views\entities\redactor\Redactor.vue             |
| Page Contexte de publication | src\main\webapp\scripts\app\entities\publisher\publisher.js       | src\main\webapp\src\views\entities\publisher\Publisher.vue           |
| Page Mes publications        | src\main\webapp\scripts\app\manager\contents\owned\owned.js       | src\main\webapp\src\views\manager\contents\owned\Owned.vue           |
| Page Toutes les publications | src\main\webapp\scripts\app\manager\contents\managed\managed.js   | src\main\webapp\src\views\manager\contents\managed\Managed.vue       |
| Page Détails de publication  | src\main\webapp\scripts\app\manager\contents\details\details.js   | src\main\webapp\src\views\manager\contents\details\ContentDetail.vue |
| Page Gestion des contextes   | src\main\webapp\scripts\app\manager\treeview\treeview.js          | src\main\webapp\src\views\manager\treeview\Treeview.vue              |
| Modal Détails de sujet       | src\main\webapp\scripts\app\entities\subject\subject.js           | src\main\webapp\src\views\entities\subject\SubjectDetail.vue         |
| Filtre Truncate              | src\main\webapp\scripts\components\util\truncate.filter.js        | src\main\webapp\src\services\util\TruncateUtils.js                   |
| Directive active-menu        | src\main\webapp\scripts\components\navbar\navbar.directive.js     | src\main\webapp\src\directives\NavbarDirective.js                    |
| Directive has-any-role       | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\HasAnyRoleDirective.js                |
| Directive has-role           | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\HasRoleDirective.js                   |
| Directive can-moderate       | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\CanModerateDirective.js               |
| Directive can-edit           | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\CanEditDirective.js                   |
| Directive can-edit-targets   | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\CanEditTargetsDirective.js            |
| Directive can-delete         | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\CanDeleteDirective.js                 |
| Directive can-highlight      | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\CanHighlightDirective.js              |
| Directive can-create-in      | src\main\webapp\scripts\components\auth\authority.directive.js    | src\main\webapp\src\directives\CanCreateInDirective.js               |
| Directive disable-click      | src\main\webapp\scripts\components\form\disableClick.directive.js | src\main\webapp\src\directives\DisableClickDirective.js              |

## Remarques

### Bootstrap

Passage de Bootstrap 3.3 à Bootstrap 5.1.

### Font-Awesome

Passage de Font-Awesome 4.5 à Font-Awesome 6.1.

### MaterialDesignIcons

Suppression de mdi au profit de Font-Awesome.

Tableau de correspondance Classes Mdi / Classes Font-Awesome (pour la migration en base de données):

| Mdi                                     | Font-Awesome                 |
| --------------------------------------- | ---------------------------- |
| mdi mdi-file mdi-dark mdi-lg            | fas fa-file fa-lg            |
| mdi mdi-file-image mdi-dark mdi-lg      | fas fa-file-image fa-lg      |
| mdi mdi-file-video mdi-dark mdi-lg      | fas fa-file-video fa-lg      |
| mdi mdi-file-music mdi-dark mdi-lg      | fas fa-file-audio fa-lg      |
| mdi mdi-file-pdf mdi-dark mdi-lg        | fas fa-file-pdf fa-lg        |
| mdi mdi-file-word mdi-dark mdi-lg       | fas fa-file-word fa-lg       |
| mdi mdi-file-excel mdi-dark mdi-lg      | fas fa-file-excel fa-lg      |
| mdi mdi-file-powerpoint mdi-dark mdi-lg | fas fa-file-powerpoint fa-lg |
| mdi mdi-file-document mdi-dark mdi-lg   | fas fa-file-lines fa-lg      |

### Swagger-ui

Conservation de la version 2.0.24 de Swagger UI lors de la migration vers Vue.
Un passage vers à la dernière version de Swagger UI (4.0.0) nécessite une mise à jour de l'Api fournie par le back-end (cf. https://github.com/swagger-api/swagger-ui#compatibility)

### Editeur de texte WYSIWYG

Remplacement de l'éditeur textAngular par l'éditeur CKEditor 5 (documentation : https://ckeditor.com/docs/ckeditor5/latest/index.html).

L'éditeur CKEditor 5 est configuré dans le composant src\components\richtext\RichText.vue.

### Cropping d'image

Remplacement de la librairie ui-cropper par la librairie vue-cropperjs.

### Web-components

Utilisation des web-components suivants :

-   @gip-recia/js-tree : Remplace la librairie jstree
-   @gip-recia/color-palette-picker : Remplace la directive AngularJs color-palette-picker
-   @gip-recia/subject-infos : Remplace la directive AngularJs subject-infos
-   @gip-recia/subject-search-button : Remplace la directive AngularJs subject-search-button
-   @gip-recia/evaluator : Remplace les directives AngularJs evaluator et edit-evaluator

Source des web-components : https://github.com/GIP-RECIA/esup-publisher-webcomponents

### Variabilisation du thème CSS

Des variables CSS ont été créées afin de faciliter la modification du thème du site. Variables disponibles :

| Nom                                                                | Valeur par défaut | Description                                                                                       |
| ------------------------------------------------------------------ | ----------------- | ------------------------------------------------------------------------------------------------- |
| --theme-link-focus-color                                           | #25B2F3           | Couleur des liens survolés ou ayant le focus                                                      |
| --theme-link-focus-background-color                                | transparent       | Couleur d'arrière-plan des liens survolés ou ayant le focus                                       |
| --theme-pagination-link-color                                      | #25B2F3           | Couleur des liens dans les paginations                                                            |
| --theme-pagination-active-link-color                               | #fff              | Couleur du lien actif dans les paginations                                                        |
| --theme-pagination-active-link-background-color                    | #25B2F3           | Couleur d'arrière-plan du lien actif dans les paginations                                         |
| --theme-tabs-link-focus-color                                      | #25B2F3           | Couleur des liens des onglets (de type tabs) survolés ou ayant le focus                           |
| --theme-tabs-active-link-color                                     | #fff              | Couleur des liens des onglets (de type tabs) actifs                                               |
| --theme-tabs-active-link-background-color                          | #25B2F3           | Couleur d'arrière-plan des liens des onglets (de type tabs) actifs                                |
| --theme-pills-link-focus-color                                     | #25B2F3           | Couleur des liens des onglets (de type pills) survolés ou ayant le focus                          |
| --theme-pills-active-link-color                                    | #fff              | Couleur des liens des onglets (de type pills) actifs                                              |
| --theme-pills-active-link-background-color                         | #25B2F3           | Couleur d'arrière-plan des liens des onglets (de type pills) actifs                               |
| --theme-btn-nav-focus-color                                        | #fff              | Couleur du texte des boutons de navigation survolés ou ayant le focus                             |
| --theme-btn-nav-focus-background-color                             | #25B2F3           | Couleur d'arrière-plan des boutons de navigation survolés ou ayant le focus                       |
| --theme-dropdown-menu-link-focus-color                             | #25B2F3           | Couleur des liens des listes déroulantes survolés ou ayant le focus                               |
| --theme-dropdown-menu-link-focus-background-color                  | #e9ecef           | Couleur d'arrière-plan des liens des listes déroulantes survolés ou ayant le focus                |
| --theme-dropdown-menu-active-link-color                            | #fff              | Couleur des liens des listes déroulantes actifs                                                   |
| --theme-dropdown-menu-active-link-background-color                 | #25B2F3           | Couleur d'arrière-plan des liens des listes déroulantes actifs                                    |
| --theme-navbar-collapse-border-color                               | #25B2F3           | Couleur des bordures des sous-menus actifs                                                        |
| --theme-publish-active-step-number-color                           | #fff              | Couleur du numéro de l'étape de publication actuelle                                              |
| --theme-publish-active-step-number-background-color                | #25B2F3           | Couleur d'arrière-plan du numéro de l'étape de publication actuelle                               |
| --theme-publish-title-color                                        | #25B2F3           | Couleur du titre de l'étape de publication actuelle                                               |
| --theme-publish-classification-iconurl-color                       | #25B2F3           | Couleur des icônes des rubriques de publication                                                   |
| --theme-publish-content-checkbox-color                             | #25B2F3           | Couleur des checkboxes dans le formulaire de création/modification de contenu                     |
| --theme-js-tree-hover-text-color                                   | #25B2F3           | Couleur du texte des entrées de l'arborescence survolées                                          |
| --theme-js-tree-selected-text-color                                | #fff              | Couleur du texte des entrées de l'arborescence sélectionnées                                      |
| --theme-js-tree-selected-background-color                          | #25B2F3           | Couleur d'arrière-plan des entrées de l'arborescence sélectionnées                                |
| --theme-subject-infos-focus-text-color                             | #25B2F3           | Couleur du texte des subject-infos survolés ou ayant le focus                                     |
| --theme-subject-infos-focus-text-background-color                  | transparent       | Couleur d'arrière-plan des composants subject-infos survolés ou ayant le focus                    |
| --theme-subject-search-button-pagination-text-color                | #25B2F3           | Couleur des liens dans les paginations des composants subject-search-button                       |
| --theme-subject-search-button-pagination-selected-background-color | #25B2F3           | Couleur d'arrière-plan des liens actifs dans les paginations des composants subject-search-button |
| --theme-subject-search-button-pagination-selected-text-color       | #fff              | Couleur des liens actifs dans les paginations des composants subject-search-button                |
| --theme-subject-search-button-validate-button-text-color           | #fff              | Couleur du texte du bouton de validation des modales des composants subject-search-button         |
| --theme-subject-search-button-validate-button-background-color     | #25B2F3           | Couleur de fond du bouton de validation des modales des composants subject-search-button          |
| --theme-subject-search-button-validate-button-border-color         | #25B2F3           | Couleur de la bordure du bouton de validation des modales des composants subject-search-button    |

### GitHub Actions

Mise à jour du script pour l'exécution des GitHub Actions pour utiliser les versions de NodeJS 16 et 17 et pour exécuter les tests unitaires du front.
