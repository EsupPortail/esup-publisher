'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('userGroupEvaluator', {
                parent: 'entity',
                url: '/userGroupEvaluator',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.userGroupEvaluator.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/userGroupEvaluator/userGroupEvaluators.html',
                        controller: 'UserGroupEvaluatorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userGroupEvaluator');
                        return $translate.refresh();
                    }]
                }
            })
            .state('userGroupEvaluatorDetail', {
                parent: 'entity',
                url: '/userGroupEvaluator/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.userGroupEvaluator.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/userGroupEvaluator/userGroupEvaluator-detail.html',
                        controller: 'UserGroupEvaluatorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userGroupEvaluator');
                        return $translate.refresh();
                    }]
                }
            });
    });
