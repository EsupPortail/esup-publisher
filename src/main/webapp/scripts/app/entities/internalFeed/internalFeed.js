'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('internalFeed', {
                parent: 'entity',
                url: '/internalFeed',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.internalFeed.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/internalFeed/internalFeeds.html',
                        controller: 'InternalFeedController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('internalFeed');
                        return $translate.refresh();
                    }]
                }
            })
            .state('internalFeedDetail', {
                parent: 'entity',
                url: '/internalFeed/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.internalFeed.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/internalFeed/internalFeed-detail.html',
                        controller: 'InternalFeedDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('internalFeed');
                        return $translate.refresh();
                    }]
                }
            });
    });
