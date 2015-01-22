'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('treeview', {
                parent: 'manager',
                url: '/treeview',
                views: {
                    'content@manager': {
                        templateUrl: 'scripts/app/manager/treeview/treeview.html',
                        controller: 'MainTreeViewController'
                    }
                },
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'manager.treeview.title'
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('manager');
                        return $translate.refresh();
                    }]
                }
            })
    });
