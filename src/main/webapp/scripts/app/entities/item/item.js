'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('item', {
                parent: 'entity',
                url: '/item',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.item.home.title'
                },
                views: {
                    'content@admin': {
                        templateUrl: 'scripts/app/entities/item/items.html',
                        controller: 'ItemController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('item');
                        $translatePartialLoader.addPart('news');
                        $translatePartialLoader.addPart('media');
                        $translatePartialLoader.addPart('resource');
                        return $translate.refresh();
                    }]
                }
            });
    });
