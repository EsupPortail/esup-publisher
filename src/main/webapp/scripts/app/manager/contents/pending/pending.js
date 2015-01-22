'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('pending', {
                parent: 'contents',
                url: '/contents/pending',
                views: {
                    'content@manager': {
                        templateUrl: 'scripts/app/manager/contents/pending/pending.html',
                        controller: 'PendingController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('item');
                        return $translate.refresh();
                    }]
                }
            });
    });
