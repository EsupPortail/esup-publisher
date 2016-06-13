'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('manager', {
                abstract: true,
                parent: 'site',
                template: '<div ui-view></div>',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'navbar@': {
                        templateUrl: 'scripts/components/navbar/navbarmanager.html',
                        controller: 'NavbarController'
                    },
                    'container@': {
                        templateUrl: 'scripts/app/manager/manager.html',
                        controller: 'ManagerController'
                    }
                },
                resolve: {
                    dataResolved: [ '$q', 'Organization', 'Redactor', function ($q, Organization, Redactor) {
                        var organizationList = Organization.query();
                        var redactorList = Redactor.query();
                        return $q.all([organizationList.$promise, redactorList.$promise]);
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('manager');
                        $translatePartialLoader.addPart('error');
                        return $translate.refresh();
                    }]
                }
            });
    });
