'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('authenticatedUserEvaluator', {
                parent: 'entity',
                url: '/authenticatedUserEvaluator',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.authenticatedUserEvaluator.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/authenticatedUserEvaluator/authenticatedUserEvaluators.html',
                        controller: 'AuthenticatedUserEvaluatorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('authenticatedUserEvaluator');
                        return $translate.refresh();
                    }]
                }
            })
            .state('authenticatedUserEvaluatorDetail', {
                parent: 'entity',
                url: '/authenticatedUserEvaluator/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.authenticatedUserEvaluator.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/authenticatedUserEvaluator/authenticatedUserEvaluator-detail.html',
                        controller: 'AuthenticatedUserEvaluatorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('authenticatedUserEvaluator');
                        return $translate.refresh();
                    }]
                }
            });
    });
