'use strict';
angular.module('publisherApp')
    .controller('PublishController', function ($scope, $state, contentData, ContentDTO) {
        //$scope.publisher = {};
        //$scope.item = {};
        //$scope.classifications = [];
        //$scope.targets = [];
        $scope.itemValidated = false;
        $scope.classificationsValidated = false;


        $scope.publishingType = function() {
            if (!$scope.isPublisherSelected()) {
                //console.log("publishingType = 'NONE'");
                return "NONE";
            }
            return $scope.publisher.context.redactor.writingMode;
        };

        $scope.isPublisherSelected = function() {
            return $scope.publisher && !angular.equals({},$scope.publisher) && $scope.publisher.id;
        };

        $scope.isItemValidated = function() {
            return $scope.itemValidated;
        };

        $scope.isClassificationsSelected = function() {
            return $scope.classifications.length > 0;
        };


        $scope.canSubmit = function() {
            //console.log("canSubmit ?",$scope.itemValidated, $scope.classifications, $scope.publisher.context.redactor.writingMode, $scope.targets);
            return $scope.publisher && $scope.publisher.id
                && $scope.itemValidated
                && $scope.classifications && $scope.classifications.length > 0
                && ($scope.publisher.context.redactor.writingMode == "STATIC" || ($scope.targets && $scope.targets.length > 0));
        };

        $scope.canSave = function() {
            return $scope.publisher && $scope.publisher.id && $scope.classificationsValidated && $scope.classifications.length > 0
                && angular.isDefined($scope.item) && angular.isDefined($scope.item.title);
        };

        $scope.publishItem = function (status) {
            switch (status) {
                case 'PUBLISH':
                    $scope.item.status = "PUBLISHED";
                    break;
                default:
                    //DRAFT
                    $scope.item.status = "DRAFT";
                    break;
            }
            var targets = [];
            for(var i = 0; i < $scope.targets.length; ++i) {
                targets.push({
                    subject: {
                        modelId: $scope.targets[i].modelId,
                        displayName: null,
                        foundOnExternalSource: null
                    }, subscribeType: "FORCED"
                });
            }

            var content = {
                //publisher : $sope.publisher,
                classifications: $scope.classifications,
                item : $scope.item,
                targets: targets
            };
            //console.log("publishing : " + JSON.stringify(content));
            if (content.item.id != null) {
                ContentDTO.update(content,
                    function () {
                        // TODO modale Success ?
                        $state.go("owned");
                    });
            } else {
                ContentDTO.save(content,
                    function () {
                        // TODO modale Success ?
                        $state.go("owned");
                    });
            }
        };

    });
