'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('userAttributesEvaluator', {
                parent: 'entity',
                url: '/userAttributesEvaluator',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.userAttributesEvaluator.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/userAttributesEvaluator/userAttributesEvaluators.html',
                        controller: 'UserAttributesEvaluatorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userAttributesEvaluator');
                        return $translate.refresh();
                    }]
                }
            })
            .state('userAttributesEvaluatorDetail', {
                parent: 'entity',
                url: '/userAttributesEvaluator/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.userAttributesEvaluator.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/userAttributesEvaluator/userAttributesEvaluator-detail.html',
                        controller: 'UserAttributesEvaluatorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userAttributesEvaluator');
                        return $translate.refresh();
                    }]
                }
            });
    });
