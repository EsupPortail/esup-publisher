'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('permissionOnClassificationWithSubjectList', {
                parent: 'entity',
                url: '/permissionOnClassificationWithSubjectList',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnClassificationWithSubjectList.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnClassificationWithSubjectList/permissionOnClassificationWithSubjectLists.html',
                        controller: 'PermissionOnClassificationWithSubjectListController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnClassificationWithSubjectList');
                        return $translate.refresh();
                    }]
                }
            })
            .state('permissionOnClassificationWithSubjectListDetail', {
                parent: 'entity',
                url: '/permissionOnClassificationWithSubjectList/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.permissionOnClassificationWithSubjectList.detail.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/permissionOnClassificationWithSubjectList/permissionOnClassificationWithSubjectList-detail.html',
                        controller: 'PermissionOnClassificationWithSubjectListDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('permissionOnClassificationWithSubjectList');
                        return $translate.refresh();
                    }]
                }
            });
    });
