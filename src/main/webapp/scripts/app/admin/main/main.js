'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('administration', {
                parent: 'admin',
                url: '/administration',
                data: {
                    pageTitle: 'admin.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/admin/main/main.html',
                        controller: 'AdminMainController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('admin');
                        return $translate.refresh();
                    }]
                }
            });
    });
