'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('contents', {
                abstract: true,
                parent: 'manager',
                data: {
                    pageTitle: 'manager.contents.title'
                }
            });
    });
