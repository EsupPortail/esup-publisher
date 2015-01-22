'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('media', {
                parent: 'entity',
                url: '/media',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.media.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/media/medias.html',
                        controller: 'MediaController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('media');
                        return $translate.refresh();
                    }]
                }
            })
            .state('mediaDetail', {
                parent: 'entity',
                url: '/media/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.media.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/media/media-detail.html',
                        controller: 'MediaDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('media');
                        return $translate.refresh();
                    }]
                }
            });
    });
