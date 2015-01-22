'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('userMultivaluedAttributesEvaluator', {
                parent: 'entity',
                url: '/userMultivaluedAttributesEvaluator',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.userMultivaluedAttributesEvaluator.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/userMultivaluedAttributesEvaluator/userMultivaluedAttributesEvaluators.html',
                        controller: 'UserMultivaluedAttributesEvaluatorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userMultivaluedAttributesEvaluator');
                        return $translate.refresh();
                    }]
                }
            })
            .state('userMultivaluedAttributesEvaluatorDetail', {
                parent: 'entity',
                url: '/userMultivaluedAttributesEvaluator/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.userMultivaluedAttributesEvaluator.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/userMultivaluedAttributesEvaluator/userMultivaluedAttributesEvaluator-detail.html',
                        controller: 'UserMultivaluedAttributesEvaluatorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('userMultivaluedAttributesEvaluator');
                        return $translate.refresh();
                    }]
                }
            });
    });
