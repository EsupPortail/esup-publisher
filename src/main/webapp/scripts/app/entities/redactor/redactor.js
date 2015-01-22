'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('redactor', {
                parent: 'entity',
                url: '/redactor',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.redactor.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/redactor/redactors.html',
                        controller: 'RedactorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('redactor');
                        return $translate.refresh();
                    }]
                }
            })
            .state('redactorDetail', {
                parent: 'entity',
                url: '/redactor/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.redactor.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/redactor/redactor-detail.html',
                        controller: 'RedactorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('redactor');
                        return $translate.refresh();
                    }]
                }
            });
    });
