'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('permissionOnContext', {
                parent: 'entity',
                url: '/permissionOnContext',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnContext.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnContext/permissionOnContexts.html',
                        controller: 'PermissionOnContextController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnContext');
                        return $translate.refresh();
                    }]
                }
            })
            .state('permissionOnContextDetail', {
                parent: 'entity',
                url: '/permissionOnContext/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnContext.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnContext/permissionOnContext-detail.html',
                        controller: 'PermissionOnContextDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnContext');
                        return $translate.refresh();
                    }]
                }
            });
    });
