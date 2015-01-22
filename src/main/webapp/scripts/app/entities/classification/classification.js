'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('classification', {
                parent: 'entity',
                url: '/classification',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.classification.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/classification/classifications.html',
                        controller: 'ClassificationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('classification');
                        $translatePartialLoader.addPart('category');
                        $translatePartialLoader.addPart('externalFeed');
                        $translatePartialLoader.addPart('internalFeed');
                        return $translate.refresh();
                    }]
                }
            });
    });
