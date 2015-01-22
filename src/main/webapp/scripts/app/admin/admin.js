'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('admin', {
                abstract: true,
                parent: 'site',
                data: {
                    roles: ['ROLE_ADMIN']
                },
                views: {
                    'navbar@': {
                        templateUrl: 'scripts/components/navbar/navbar.html',
                        controller: 'NavbarController'
                    },
                    'container@': {
                        templateUrl: 'scripts/app/admin/admin.html',
                        controller: 'AdminController'
                    }
                }
            });
    });
