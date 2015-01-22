'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('subscriber', {
                parent: 'entity',
                url: '/subscriber',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.subscriber.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/subscriber/subscribers.html',
                        controller: 'SubscriberController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('subscriber');
                        return $translate.refresh();
                    }]
                }
            })
            .state('subscriberDetail', {
                parent: 'entity',
                url: '/subscriber/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.subscriber.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/subscriber/subscriber-detail.html',
                        controller: 'SubscriberDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('subscriber');
                        return $translate.refresh();
                    }]
                }
            });
    });
