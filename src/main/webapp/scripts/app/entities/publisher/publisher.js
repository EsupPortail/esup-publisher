'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('publisher', {
                parent: 'entity',
                url: '/publisher',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.publisher.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/publisher/publishers.html',
                        controller: 'PublisherController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('publisher');
                        return $translate.refresh();
                    }],
                    dataResolved: [ '$q', 'Organization', 'Redactor', 'Reader', function ($q, Organization, Redactor, Reader) {
                        var organizationList = Organization.query();
                        var redactorList = Redactor.query();
                        var readerList = Reader.query();
                        return $q.all([organizationList.$promise, redactorList.$promise, readerList.$promise]);
                    }]
                }
            })
            .state('publisherDetail', {
                parent: 'entity',
                url: '/publisher/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.publisher.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/publisher/publisher-detail.html',
                        controller: 'PublisherDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('publisher');
                        return $translate.refresh();
                    }]
                }
            });
    });
