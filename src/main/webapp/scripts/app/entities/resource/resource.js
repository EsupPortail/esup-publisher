'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('resource', {
                parent: 'entity',
                url: '/resource',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.resource.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/resource/resources.html',
                        controller: 'ResourceController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('resource');
                        return $translate.refresh();
                    }]
                }
            })
            .state('resourceDetail', {
                parent: 'entity',
                url: '/resource/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.resource.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/resource/resource-detail.html',
                        controller: 'ResourceDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('resource');
                        return $translate.refresh();
                    }]
                }
            });
    });
