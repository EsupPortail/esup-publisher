'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('filter', {
                parent: 'entity',
                url: '/filter',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.filter.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/filter/filters.html',
                        controller: 'FilterController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('filter');
                        return $translate.refresh();
                    }],
                    dataResolved: [ '$q', 'Organization', function ($q, Organization) {
                        var organizationList = Organization.query();
                        return $q.all([organizationList.$promise]);
                    }]
                }
            })
            .state('filterDetail', {
                parent: 'entity',
                url: '/filter/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.filter.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/filter/filter-detail.html',
                        controller: 'FilterDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('filter');
                        return $translate.refresh();
                    }]
                }
            });
    });
