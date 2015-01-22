'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('reader', {
                parent: 'entity',
                url: '/reader',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.reader.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/reader/readers.html',
                        controller: 'ReaderController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reader');
                        $translatePartialLoader.addPart('enum');
                        return $translate.refresh();
                    }]
                }
            })
            .state('readerDetail', {
                parent: 'entity',
                url: '/reader/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.reader.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/reader/reader-detail.html',
                        controller: 'ReaderDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reader');
                        return $translate.refresh();
                    }]
                }
            });
    });
