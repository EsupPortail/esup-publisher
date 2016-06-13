'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('managed', {
                parent: 'contents',
                url: '/contents/managed',
                views: {
                    'content@manager': {
                        templateUrl: 'scripts/app/manager/contents/managed/managed.html',
                        controller: 'ManagedController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('item');
                        $translatePartialLoader.addPart('classification');
                        return $translate.refresh();
                    }]
                }
            });
    });
