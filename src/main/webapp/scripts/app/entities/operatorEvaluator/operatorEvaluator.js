'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('operatorEvaluator', {
                parent: 'entity',
                url: '/operatorEvaluator',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.operatorEvaluator.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/operatorEvaluator/operatorEvaluators.html',
                        controller: 'OperatorEvaluatorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('operatorEvaluator');
                        return $translate.refresh();
                    }]
                }
            })
            .state('operatorEvaluatorDetail', {
                parent: 'entity',
                url: '/operatorEvaluator/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.operatorEvaluator.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/operatorEvaluator/operatorEvaluator-detail.html',
                        controller: 'OperatorEvaluatorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('operatorEvaluator');
                        return $translate.refresh();
                    }]
                }
            });
    });
