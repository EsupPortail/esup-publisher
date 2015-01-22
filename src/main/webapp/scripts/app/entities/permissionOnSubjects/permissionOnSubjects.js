'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('permissionOnSubjects', {
                parent: 'entity',
                url: '/permissionOnSubjects',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnSubjects.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnSubjects/permissionOnSubjectss.html',
                        controller: 'PermissionOnSubjectsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnSubjects');
                        return $translate.refresh();
                    }]
                }
            })
            .state('permissionOnSubjectsDetail', {
                parent: 'entity',
                url: '/permissionOnSubjects/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnSubjects.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnSubjects/permissionOnSubjects-detail.html',
                        controller: 'PermissionOnSubjectsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnSubjects');
                        return $translate.refresh();
                    }]
                }
            });
    });
