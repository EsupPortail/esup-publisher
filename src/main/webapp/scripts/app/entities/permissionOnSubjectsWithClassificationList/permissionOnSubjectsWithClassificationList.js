'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('permissionOnSubjectsWithClassificationList', {
                parent: 'entity',
                url: '/permissionOnSubjectsWithClassificationList',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnSubjectsWithClassificationList.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnSubjectsWithClassificationList/permissionOnSubjectsWithClassificationLists.html',
                        controller: 'PermissionOnSubjectsWithClassificationListController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnSubjectsWithClassificationList');
                        return $translate.refresh();
                    }]
                }
            })
            .state('permissionOnSubjectsWithClassificationListDetail', {
                parent: 'entity',
                url: '/permissionOnSubjectsWithClassificationList/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnSubjectsWithClassificationList.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnSubjectsWithClassificationList/permissionOnSubjectsWithClassificationList-detail.html',
                        controller: 'PermissionOnSubjectsWithClassificationListDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnSubjectsWithClassificationList');
                        return $translate.refresh();
                    }]
                }
            });
    });
