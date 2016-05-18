'use strict';

angular.module('publisherApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('publish', {
                parent: 'manager',
                url: '/publish/:id',
                views: {
                    'content@manager': {
                        templateUrl: 'scripts/app/manager/publish/publish.html',
                        controller: 'PublishController'
                    }
                },
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'manager.publish.title'
                },
                resolve: {
                    contentResource : 'ContentDTO',
                    contentData: function(contentResource, $stateParams) {
                        if ($stateParams.id) {
                            return contentResource.get({id: $stateParams.id}).$promise;
                        }
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        $translatePartialLoader.addPart('publisher');
                        $translatePartialLoader.addPart('item');
                        $translatePartialLoader.addPart('flash');
                        $translatePartialLoader.addPart('news');
                        $translatePartialLoader.addPart('media');
                        $translatePartialLoader.addPart('resource');
                        $translatePartialLoader.addPart('manager');
                        return $translate.refresh();
                    }]
                }
            })
            .state('publish.publisher', {
                //parent: 'publish',
                url: '/publisher',
                templateUrl: 'scripts/app/manager/publish/publisher/publisher.html',
                controller: 'PublisherChoiceController',
                resolve: {
                    classificationResource : 'Classification',
                    loadedPublisher : function(contentData, classificationResource){
                        if (contentData && contentData.classifications && contentData.classifications.length > 0) {
                           return classificationResource.get({id: contentData.classifications[0].keyId}).$promise;
                        }
                    },
                    publisherResource: 'Publisher',
                    loadedPublishers : function(publisherResource){
                        return  publisherResource.query({used: true}).$promise;
                    }
                }
            })
            .state('publish.content', {
                parent: 'publish',
                url: '/content',
                templateUrl: 'scripts/app/manager/publish/content/content.html',
                controller: 'ContentWriteController',
                resolve: {
                    loadedItem : function(contentData){
                        if (contentData)
                            return contentData.item;
                    }
                }
            })
            .state('publish.classification', {
                parent: 'publish',
                url: '/classification',
                templateUrl: 'scripts/app/manager/publish/classification/classification.html',
                controller: 'ClassificationChoiceController',
                resolve: {
                    loadedClassifications: function(contentData){
                        if (contentData)
                            return contentData.classifications;
                    }
                }
            })
            .state('publish.targets', {
                parent: 'publish',
                url: '/targets',
                templateUrl: 'scripts/app/manager/publish/target/targets.html',
                controller: "TargetsPublishController",
                resolve: {
                    dataResolved: ['$q', 'Subject', function($q, Subject) {
                        Subject.init();
                    }],
                    loadedTargets: function(contentData){
                        if (contentData)
                            return contentData.targets;
                    }
                }
            })
            /*.state('publish.details', {
                parent: 'publish',
                url: '/details',
                templateUrl: 'scripts/app/manager/publish/publish-details.html'
            })*/
            .state('publish.targets.subject', {
                parent: 'publish.targets',
                url: '/subject/:keyType/:keyId',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'publisherApp.subject.detail.title'
                },
                onEnter: function($stateParams, $state, $uibModal, $resource, Subject) {
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
                            console.log("subject controller");
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
                            $state.go('publish.targets');
                        },
                        function (result) {
                            $state.go('publish.targets');
                        }
                    );
                }
            });
    });
