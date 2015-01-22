'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('category', {
                parent: 'entity',
                url: '/category',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.category.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/category/categorys.html',
                        controller: 'CategoryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('category');
                        return $translate.refresh();
                    }],
                    dataResolved: [ '$q', 'Organization', 'Publisher', function ($q, Organization, Publisher) {
                        var organizationList = Organization.query();
                        var publisherList = Publisher.query();
                        return $q.all([organizationList.$promise, publisherList.$promise]);
                    }]
                }
            })
            .state('categoryDetail', {
                parent: 'entity',
                url: '/category/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.category.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/category/category-detail.html',
                        controller: 'CategoryDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('category');
                        return $translate.refresh();
                    }]
                }
            });
    });
