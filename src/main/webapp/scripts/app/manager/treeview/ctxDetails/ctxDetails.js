'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ctxDetails', {
                parent: 'treeview',
                url: '/details/:ctxType/:ctxId',
                views: {
                    'contextDetails': {
                        templateUrl: 'scripts/app/manager/treeview/ctxDetails/ctxDetails.html',
                        controller: 'DetailsTreeViewController'
                    }
                },
                resolve: {
                    dataResolved: ['$q', 'Subject', function($q, Subject) {
                        Subject.init();
                        /*EnumDatas.getPermissionTypeList();
                        EnumDatas.getPermissionClassList();
                        EnumDatas.getSubscribeTypeList();
                        EnumDatas.getSubjectTypeList();
                        EnumDatas.getDisplayOrderTypeList();
                        EnumDatas.getStringEvaluationModeList();
                        EnumDatas.getOperatorTypeList();*/
                        /*$scope.userAttributes = User.funtionalAttributes();
                        var organizationList = Organization.query();
                        var redactorList = Redactor.query();
                        var readerList = Reader.query();
                        return $q.all([organizationList.$promise, redactorList.$promise, readerList.$promise]);*/
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('manager');
                        $translatePartialLoader.addPart('organization');
                        $translatePartialLoader.addPart('publisher');
                        $translatePartialLoader.addPart('category');
                        $translatePartialLoader.addPart('internalFeed');
                        $translatePartialLoader.addPart('externalFeed');
                        $translatePartialLoader.addPart('item');
                        $translatePartialLoader.addPart('news');
                        $translatePartialLoader.addPart('media');
                        $translatePartialLoader.addPart('resource');
                        $translatePartialLoader.addPart('permission');
                        $translatePartialLoader.addPart('permissionOnContext');
                        $translatePartialLoader.addPart('permissionOnClassificationWithSubjectList');
                        $translatePartialLoader.addPart('subscriber');
                        $translatePartialLoader.addPart('evaluators');
                        return $translate.refresh();
                    }]
                }
            }).state('ctxDetails.subject', {
                parent: 'ctxDetails',
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
                                var attributes = ($stateParams.type == "PERSON") ? Subject.getUserDisplayedAttrs() : Subject.getGroupDisplayedAttrs();
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
                            $state.go('ctxDetails');
                        },
                        function (result) {
                            $state.go('ctxDetails');
                        }
                    );
                }
            });

    });
