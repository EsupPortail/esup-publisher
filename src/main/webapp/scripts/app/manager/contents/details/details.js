'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('details', {
                parent: 'contents',
                url: '/contents/details/:id',
                views: {
                    'content@manager': {
                        templateUrl: 'scripts/app/manager/contents/details/details.html',
                        controller: 'ContentDetailsController'
                    }
                },
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'manager.contents.details.title'
                },
                resolve: {
                    contentResource : 'ContentDTO',
                    classifRessource : 'Classification',
                    contentData: function(contentResource, classifRessource, $stateParams, $q) {
                        if ($stateParams.id) {
                            return contentResource.get({id: $stateParams.id}).$promise.then(function(result){
                                console.log(result);
                                var classifs = [];
                                if (result) {
                                    angular.forEach(result.classifications, function(value, key) {
                                        console.log("loop",value);
                                        classifs.push(classifRessource.get({id: value.keyId}).$promise);
                                    })
                                }
                                //classifs.push(result);
                                return $q.all(classifs).then(function(resolvedClassifs) {
                                    return [resolvedClassifs, result];
                                }, function(){
                                    return [[], result];
                                });
                            }, function(error){
                                return null;
                            });
                        }
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('manager');
                        $translatePartialLoader.addPart('organization');
                        $translatePartialLoader.addPart('publisher');
                        $translatePartialLoader.addPart('classification');
                        $translatePartialLoader.addPart('category');
                        $translatePartialLoader.addPart('internalFeed');
                        $translatePartialLoader.addPart('externalFeed');
                        $translatePartialLoader.addPart('item');
                        $translatePartialLoader.addPart('news');
                        $translatePartialLoader.addPart('media');
                        $translatePartialLoader.addPart('resource');
                        $translatePartialLoader.addPart('subscriber');
                        return $translate.refresh();
                    }]
                }
            }).state('details.subject', {
                parent: 'details',
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
                            $state.go('details');
                        },
                        function (result) {
                            $state.go('details');
                        }
                    );
                }
            });
    });
