'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('subjectDetail', {
                parent: 'site',
                url: '/subject/:type/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.subject.detail.title'
                },
                views: {
                    'modalRoot' : {
                        controller : 'SubjectDetailController',
                        templateProvider : function ($stateParams) {
                            console.log("calculate template url with param : " + JSON.stringify($stateParams.type));
                            switch ($stateParams.type) {
                                case "PERSON":
                                    return 'scripts/app/entities/subject/user-detail.html';
                                case "GROUP":
                                    return 'scripts/app/entities/subject/group-detail.html';
                            }
                        }
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('subject');
                        return $translate.refresh();
                    }]
                }
            });
    });
