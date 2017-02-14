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
            }).state('pending.subject', {
                parent: 'pending',
                url: '/subject/:keyType/:keyId',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.subject.detail.title'
                },
                onEnter: function($stateParams, $state, $uibModal, Subject) {
                    var modalInstance = $uibModal.open({
                        templateUrl: function () {
                            switch ($stateParams.keyType) {
                                case "PERSON":
                                    return 'scripts/app/entities/subject/user-detail.html';
                                case "GROUP":
                                    return 'scripts/app/entities/subject/group-detail.html';
                                default: return 'scripts/app/error/error.html'
                            }
                        },
                        resolve: {
                            dataResolved: ['$q', 'Subject', function($q, Subject) {
                                Subject.init();
                                var attributes = ($stateParams.keyType == "PERSON") ? Subject.getUserDisplayedAttrs() : Subject.getGroupDisplayedAttrs();
                                var subject =  Subject.getSubjectInfos($stateParams.keyType, $stateParams.keyId);
                                return $q.all([subject.$promise, attributes]);
                            }],
                            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                                $translatePartialLoader.addPart('subject');
                                $translatePartialLoader.addPart('global');
                                return $translate.refresh();
                            }]
                        },
                        controller: ['$scope', 'Subject', 'dataResolved', '$uibModalInstance', function($scope, Subject, dataResolved, $uibModalInstance) {
                            $scope.subject = dataResolved[0];
                            $scope.attributes = dataResolved[1];
                            $scope.close = function() {
                                $uibModalInstance.close('close');
                            };
                            $scope.cancel = function () {
                                $uibModalInstance.dismiss('dismiss');
                            };
                        }]
                    });
                    modalInstance.result.then(
                        function (result) {
                            $state.go('pending');
                        },
                        function (result) {
                            $state.go('pending');
                        }
                    );
                }
            });
    });
