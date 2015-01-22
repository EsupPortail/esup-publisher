'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('owned', {
                parent: 'contents',
                url: '/contents/owned',
                views: {
                    'content@manager': {
                        templateUrl: 'scripts/app/manager/contents/owned/owned.html',
                        controller: 'OwnedController'
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
