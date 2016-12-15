'use strict';

angular.module('publisherApp', ['LocalStorageModule', 'tmh.dynamicLocale', 'ngResource', 'ui.router', 'checklist-model', /*'ngAnimate',*/ 'angularSpinner',/*'infinite-scroll',*/
    'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'ui.bootstrap','angular.filter', 'isteven-multi-select', 'jsTree.directive', 'ui.bootstrap.tooltip','ui.bootstrap.pagination',
    'textAngular', 'ngFileUpload', 'ngImgCrop', 'ImageCropper', 'colorpicker.module'])

    .run(function ($rootScope, $location, $window, $http, $state, $translate, Auth, Principal, Language, ENV, VERSION) {
        $rootScope.ENV = ENV;
        $rootScope.VERSION = VERSION;
        $rootScope.HOST = $location.host().split('.').join('_');
        //$rootScope.showSpinner = false;
        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
            //console.log("$stateChangeStart");
            $rootScope.toState = toState;
            $rootScope.toStateParams = toStateParams;

            /*if (toState.resolve) {
                $rootScope.showSpinner = true;
            }*/

            var requireLogin = toState.data.requireLogin;

            if (requireLogin && !Principal.isAuthenticated() && !$rootScope.modalOpened ) {
                event.preventDefault();
                Auth.login().then(function () {
                    return $state.transitionTo(toState.name, toStateParams, {reload: true});
                })
                    .catch(function () {
                        return $state.transitionTo('home', {}, {reload: true});
                    });
            }

            if (Principal.isIdentityResolved()) {
                Auth.authorize();
            }

            // Update the language
            Language.getCurrent().then(function (language) {
                $translate.use(language);
            });
        });

        $rootScope.$on('$stateChangeSuccess',  function(event, toState, toParams, fromState, fromParams) {
            //console.log("$stateChangeSuccess");
            var titleKey = 'global.title';
            $rootScope.previousStateName = fromState.name;
            $rootScope.previousStateParams = fromParams;

            /*if (toState.resolve) {
                console.log("set showspinner");
                $rootScope.showSpinner = false;
            }*/

            // Set the page title key to the one configured in state or use default one
            if (toState.data.pageTitle) {
                titleKey = toState.data.pageTitle;
            }
            $translate(titleKey).then(function (title) {
                // Change window title with translated one
                $window.document.title = title;
            });
        });

        /*$rootScope.$on('$stateChangeError', function (event, toState, toStateParams) {
            console.log("$stateChangeError");
           // $rootScope.showSpinner = false;
        });*/

        $rootScope.back = function() {
            // If previous state is 'activate' or do not exist go to 'home'
            if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
                $state.go('home');
            } else {
                $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
            }
        };

        $rootScope.inArray = function (item, array) {
            if (!angular.isDefined(array) || !angular.isArray(array)) return false;
            return (-1 !== array.indexOf(item));
        };
    })
    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, $translatePartialLoaderProvider, tmhDynamicLocaleProvider,
                      httpRequestInterceptorCacheBusterProvider, usSpinnerConfigProvider, $provide) {
        //enable CSRF
        $httpProvider.defaults.xsrfCookieName= 'CSRF-TOKEN';
        $httpProvider.defaults.xsrfHeaderName= 'X-CSRF-TOKEN';

        usSpinnerConfigProvider.setDefaults({color: 'white'});

        // configure textAngular
        $provide.decorator('taOptions', ['taRegisterTool', '$delegate','taTranslations', function(taRegisterTool, taOptions, taTranslations){

            /*['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'pre', 'quote'],
             ['bold', 'italics', 'underline', 'strikeThrough', 'ul', 'ol', 'redo', 'undo', 'clear'],
             ['justifyLeft','justifyCenter','justifyRight','justifyFull','indent','outdent'],
             ['html', 'insertImage', 'insertLink', 'insertVideo', 'wordcount', 'charcount']*/

            taOptions.toolbar = [
                /* ['h1', 'h2', 'h3', 'p', 'quote'], */
                ['textTypeDropdown'],
                ['quote','bold', 'italics' ],
                ['underline', 'strikeThrough'],
                ['ul', 'ol', 'redo', 'undo'],
                ['justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull'],
                ['backgroundColor', 'fontColor', 'clear'], /* 'fontSize', 'fontName',*/
                ['html', 'insertImage', 'insertVideo', 'insertLink']
            ];
            /*taOptions.classes = {
                focussed: "focussed",
                toolbar: "btn-toolbar",
                toolbarGroup: "btn-group",
                toolbarButton: "btn btn-primary",
                toolbarButtonActive: "active",
                disabled: "disabled",
                textEditor: 'form-control',
                htmlEditor: 'form-control'
             };*/
            // TODO i18n must find a way to provide translated values of StFormat
            // as we are in config phase we can't provide translated values
            var StFormat = "";
            var formatMethods = {};
            formatMethods.VarToH1=function(){
                this.$editor().wrapSelection("formatBlock", "<H1>");
                StFormat = 'Titre 1';
                formatMethods.changeSelected(StFormat);
            };
            formatMethods.VarToH2=function(){
                this.$editor().wrapSelection("formatBlock", "<H2>");
                StFormat = 'Titre 2';
                formatMethods.changeSelected(StFormat);
            };
            formatMethods.VarToH3=function(){
                this.$editor().wrapSelection("formatBlock", "<H3>");
                StFormat = 'Titre 3';
                formatMethods.changeSelected(StFormat);
            };
            formatMethods.VarToP=function(){
                this.$editor().wrapSelection("formatBlock", "<P>");
                StFormat = 'Paragraphe';
                formatMethods.changeSelected(StFormat);
            };
            formatMethods.changeSelected = function(Stformat){
                $('#single-button')[0].innerHTML = Stformat+'&nbsp;<span class="caret"></span>';
            };

            var templateDropdDown = '<span class="dropdown tadropdown">';
            templateDropdDown += '<button id=\"single-button\" class="textTypeDropdown btn btn-default btn-group dropdown-toggle" title="{{ \'textangular.textTypeDropdown.tooltip\' | translate }}" data-toggle="dropdown" >'
             + "{{ 'textangular.p.tooltip' | translate }}&nbsp;";
            templateDropdDown +=  "<span class=\"btCaret caret\"></span>";
            templateDropdDown += '</button>';
            templateDropdDown += '<ul class="dropdown-menu" aria-labelledby="single-button">';
            templateDropdDown += '<li><a ng-click="VarToH1()" title="{{ \'textangular.h1.tooltip\' | translate }}">{{ "textangular.h1.tooltip" | translate }}</a></li>';
            templateDropdDown += '<li class="divider"></li>';
            templateDropdDown += '<li><a ng-click="VarToH2()" title="{{ \'textangular.h2.tooltip\' | translate }}">{{ "textangular.h2.tooltip" | translate }}</a></li>';
            templateDropdDown += '<li class="divider"></li>';
            templateDropdDown += '<li><a ng-click="VarToH3()" title="{{ \'textangular.h3.tooltip\' | translate }}">{{ "textangular.h3.tooltip" | translate }}</a></li>';
            templateDropdDown += '<li class="divider"></li>';
            templateDropdDown += '<li><a ng-click="VarToP()" title="{{ \'textangular.p.tooltip\' | translate }}">{{ "textangular.p.tooltip" | translate }}</a></li>';
            templateDropdDown += '</ul>';
            templateDropdDown += '</span>';
            taRegisterTool('textTypeDropdown', {
                display: templateDropdDown,
                action: function(){
                    var self = this;
                    // insert all methods into the scope
                    Object.keys(formatMethods).forEach(function(key) {
                        self[key] = formatMethods[key];
                    });
                    return false;
                },
                activeState: function(){
                    var isH1 =  this.$editor().queryFormatBlockState('h1');
                    var isH2 =  this.$editor().queryFormatBlockState('h2');
                    var isH3 = this.$editor().queryFormatBlockState('h3');
                    var isP = this.$editor().queryFormatBlockState('p');
                    var bool = isH1 || isH2 || isH3 || isP;
                    if(isP){
                        formatMethods.changeSelected("Paragraphe");
                    }
                    if(isH3){
                        formatMethods.changeSelected("Titre 3");
                    }
                    if(isH2){
                        formatMethods.changeSelected("Titre 2");
                    }
                    if(isH1){
                        formatMethods.changeSelected("Titre 1");
                    }
                    return bool;
                }
            });
            // I18n TextAngular Adds, somes can't be applied during app.config, but redo it in controller
            angular.forEach(taTranslations, function(key, value){
                translateSubKeys(key, value, 'textangular.');
            });
            function translateSubKeys(key, value, parentPartKey) {
                angular.forEach(Object.keys(key), function(subkey, subvalue) {
                    if (typeof key[subkey] === 'object' && key[subkey] !== null) {
                        translateSubKeys(key[subkey], subvalue, parentPartKey + value + '.' + subkey + ".");
                    } else {
                        var subpath = '';
                        if (typeof value !== 'number') {
                            subpath = value + ".";
                        }
                        //console.log("translate :", parentPartKey + subpath + subkey, key[subkey]);
                            key[subkey] = "{{ '" + parentPartKey + subpath + subkey + "' | translate }}";
                            //console.log("translated :", JSON.stringify(taTranslations) );
                    }
                });
            }


            /*taRegisterTool('insertClassicVideo', {
             iconclass: 'fa fa-youtube-play',
             tooltiptext: taTranslations.insertVideo.tooltip,
             action: function(){
             var videoLink;
             videoLink = $window.prompt(taTranslations.insertImage.dialogPrompt, 'http://');
             if(videoLink && videoLink !== '' && videoLink !== 'http://'){
             return this.$editor().wrapSelection('insertVideo', videoLink, true);
             }
             },
             onElementSelect: {
             element: 'video',
             action: taToolFunctions.imgOnSelectAction
             }
             });*/

            taRegisterTool('fontColor', {
                display: "<button colorpicker type='button' title='{{ \"textangular.fontColor.tooltip\" | translate }}' colorpicker-close-on-select colorpicker-position='bottom' ng-model='fontColor' style='color: {{fontColor}}'><i class='fa fa-font'></i></button>",
                action: function (deferred) {
                    var self = this;
                    if (typeof self.listener == 'undefined') {
                        self.listener = self.$watch('fontColor', function (newValue) {
                            self.$editor().wrapSelection('foreColor', newValue);
                        });
                    }
                    self.$on('colorpicker-selected', function () {
                        deferred.resolve();
                    });
                    self.$on('colorpicker-closed', function () {
                        deferred.resolve();
                    });
                    return false;
                }
            });

            taRegisterTool('backgroundColor', {
                display: "<button colorpicker type='button' title='{{ \"textangular.backgroundColor.tooltip\" | translate }}' colorpicker-close-on-select colorpicker-position='bottom' ng-model='backgroundColor' style='background-color: {{backgroundColor}}'><i class='fa fa-paint-brush'></i></button>",
                action: function (deferred) {
                    var self = this;
                    this.$editor().wrapSelection('backgroundColor', this.backgroundColor);
                    if (typeof self.listener == 'undefined') {
                        self.listener = self.$watch('backgroundColor', function (newValue) {
                            self.$editor().wrapSelection('backColor', newValue);
                        });
                    }
                    self.$on('colorpicker-selected', function () {
                        deferred.resolve();
                    });
                    self.$on('colorpicker-closed', function () {
                        deferred.resolve();
                    });
                    return false;
                }
            });
            //taOptions.defaultFileDropHandler =
            ///* istanbul ignore next: untestable image processing */
            //function(file, insertAction){
            //    console.log("dropped file upload", file.type);
            //    var reader = new FileReader();
            //    if(file.type.substring(0, 5) === 'image'){
            //        reader.onload = function() {
            //            if(reader.result !== '') insertAction('insertImage', reader.result, true);
            //        };
            //
            //        reader.readAsDataURL(file);
            //        // NOTE: For async procedures return a promise and resolve it when the editor should update the model.
            //        return true;
            //    } else if(file.type.substring(0, 5) === 'video'){
            //        reader.onload = function() {
            //            if(reader.result !== '') insertAction('insertVideo', reader.result, true);
            //        };
            //
            //        reader.readAsDataURL(file);
            //        // NOTE: For async procedures return a promise and resolve it when the editor should update the model.
            //        return true;
            //    }
            //    return false;
            //};

            /*taOptions.toolbar[1].push('backgroundColor');
             taOptions.toolbar[1].push('fontColor');*/

            return taOptions;
        }]);

//Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $httpProvider.interceptors.push('AuthInterceptor');
//$httpProvider.interceptors.push('loadingDialogInterceptor');

        $urlRouterProvider.otherwise('/');
        $stateProvider.state('site', {
            'abstract': true,
            views: {
                /*'navbar@': {
                 templateUrl: 'scripts/components/navbar/navbar.html',
                 controller: 'NavbarController'
                 }*/
            },
            data: {
                requireLogin: true, // this property will apply to all children of 'site'
                roles: ['ROLE_USER']
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    //console.log("add partials");
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('language');
                    $translatePartialLoader.addPart('enum');
                    $translatePartialLoader.addPart('subject');
                    $translatePartialLoader.addPart('textAngular');

                    return $translate.refresh();
                }],
                initData: [ 'EnumDatas', '$q', function(EnumDatas, $q){
                    return EnumDatas.init();
                }]
            }
        });


// Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{lang}/{part}.json'
        });

        $translateProvider.registerAvailableLanguageKeys(['en', 'fr'], {
            'en_US': 'en',
            'en_UK': 'en',
            'fr_FR': 'fr'
        });

        $translateProvider.fallbackLanguage('en')
            .determinePreferredLanguage();
        //$translateProvider.useSanitizeValueStrategy('escaped');
        $translateProvider.useCookieStorage();

        $translatePartialLoaderProvider.addPart('login');

        tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
        tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
    });
