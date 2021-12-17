# Migration du front-end vers Vue

A la suite de la migration du front-end de AngularJS vers Vue, celui-ci est maintenant accessible depuis le contexte /publisher/ui.
De plus, les commandes NPM pour manipuler le front-end doivent être exécutées dans le répertoire src/main/webapps.

Les commandes disponibles sont :
- `npm install` : Installation des dépendances du front-end.
- `npm run serve` : Permet de lancer le front-end en mode développement (équivalent du `grunt serve`). Le front est alors accessible à l'adresse http://localhost:3000/publisher/ui/
- `npm run build` : Permet de construire les fichiers statiques du front-end pour la production (équivalent du `grunt build`). Les sources sont générées dans le répertoire src/main/webapps/dist.
- `npm run lint` : Permet de lancer une analyse eslint sur le code source du front-end.
- `npm run test:unit` : Permet de lancer les tests unitaires du front-end. Ces tests se trouvent dans le répertoire src/main/webapps/tests/unit.

Les commandes liées au back-end restent inchangées : 
- Lancement en local : `./mvnw clean spring-boot:run -Dmaven.test.skip=true -Pdev`
- Construction du livrable : `./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"` (le front-end est construit par Maven via le plugin frontend-maven-plugin, cf. pom.xml).

## Correspondances AngularJS - Vue

| Brique | AngularJS | Vue |
| ------ | ------ | ------ |
| Configuration Npm | package.json | src\main\webapp\package.json |
| Tests unitaires | src\test\javascript\ | src\main\webapp\tests\unit |
| Router | src\main\webapp\scripts\app\app.js | src\main\webapp\src\router\index.js |
| Internationalisation | src\main\webapp\scripts\app\app.js | src\main\webapp\src\i18n\index.js |
| Navbar | src\main\webapp\scripts\components\navbar\navbar.controller.js | src\main\webapp\src\components\navbar\NavBar.vue |
| Page Home | src\main\webapp\scripts\app\main\main.js | src\main\webapp\src\views\Home.vue |
| Page Login | src\main\webapp\scripts\app\account\login\login.js | src\main\webapp\src\views\account\login\Login.vue |
| Page Accès refusé | src\main\webapp\scripts\app\error\error.js | src\main\webapp\src\views\error\AccessDenied.vue |
| Page API | src\main\webapp\scripts\app\admin\docs\docs.js | src\main\webapp\src\views\admin\docs\AdminDocs.vue |
| Page Logs | src\main\webapp\scripts\app\admin\logs\logs.js | src\main\webapp\src\views\admin\logs\AdminLogs.vue |
| Méthode Truncate | src\main\webapp\scripts\components\util\truncate.filter.js | src\main\webapp\src\services\util\TruncateUtils.js |
| Directive active-menu | src\main\webapp\scripts\components\navbar\navbar.directive.js | src\main\webapp\src\directives\NavbarDirective.js |
| Page Structure | src\main\webapp\scripts\app\entities\organization\organization.js | src\main\webapp\src\views\entities\Organization.vue |

## Remarques

### Swagger-ui

Conservation de la version 2.0.24 de Swagger UI lors de la migration vers Vue.
Un passage vers à la dernière version de Swagger UI (4.0.0) nécessite une mise à jour de l'Api fournie par le back-end (cf. https://github.com/swagger-api/swagger-ui#compatibility)
