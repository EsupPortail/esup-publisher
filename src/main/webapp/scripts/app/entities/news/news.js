'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('news', {
                parent: 'entity',
                url: '/news',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.news.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/news/newss.html',
                        controller: 'NewsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('news');
                        return $translate.refresh();
                    }]
                }
            })
            .state('newsDetail', {
                parent: 'entity',
                url: '/news/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.news.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/news/news-detail.html',
                        controller: 'NewsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('news');
                        return $translate.refresh();
                    }]
                }
            });
    });
