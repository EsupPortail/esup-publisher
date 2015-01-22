'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('organization', {
                parent: 'entity',
                url: '/organization',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.organization.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/organization/organizations.html',
                        controller: 'OrganizationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('organization');
                        return $translate.refresh();
                    }]
                }
            })
            .state('organizationDetail', {
                parent: 'entity',
                url: '/organization/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'publisherApp.organization.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/organization/organization-detail.html',
                        controller: 'OrganizationDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('organization');
                        return $translate.refresh();
                    }]
                }
            });
    });
