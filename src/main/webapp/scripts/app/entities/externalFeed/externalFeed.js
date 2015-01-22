'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('externalFeed', {
                parent: 'entity',
                url: '/externalFeed',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.externalFeed.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/externalFeed/externalFeeds.html',
                        controller: 'ExternalFeedController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('externalFeed');
                        return $translate.refresh();
                    }]
                }
            })
            .state('externalFeedDetail', {
                parent: 'entity',
                url: '/externalFeed/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.externalFeed.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/externalFeed/externalFeed-detail.html',
                        controller: 'ExternalFeedDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('externalFeed');
                        return $translate.refresh();
                    }]
                }
            });
    });
