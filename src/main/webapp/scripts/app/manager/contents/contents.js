'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('contents', {
                abstract: true,
                parent: 'manager',
                data: {
                    requireLogin: true,
                    pageTitle: 'manager.contents.title'
                }
            });
    });
