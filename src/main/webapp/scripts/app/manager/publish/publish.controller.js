'use strict';
angular.module('publisherApp')
    .controller('PublishController', function ($scope, $state, contentData, ContentDTO) {
        //$scope.publisher = {};
        //$scope.item = {};
        //$scope.classifications = [];
        //$scope.targets = [];
        $scope.itemValidated = false;

        /*$scope.load = function(){*/
            if (angular.isDefined(contentData)) {
                console.log("editing : ", contentData);
                /*$scope.item = contentData.item;
                if (contentData.targets && contentData.targets.length > 0) {
                    $scope.targets = contentData.targets;
                }
                $scope.itemValidated = true;
                if (contentData.classifications && contentData.classifications.length > 0) {
                    $scope.classifications = contentData.classifications;
                    $scope.publisher = contentData.publisher;
                    console.log("Publisher: ", $scope.publisher);
                    console.log("classifications: ", $scope.classifications);
                    console.log("Item: ", $scope.item);
                    console.log("Targets: ", $scope.targets);

                }*/
            }
        //};

        //$scope.load();
        /* if ($stateParams.id) {
         console.log($stateParams.id);
         $scope.load($stateParams.id);
         } else {
         console.log("no $stateParams.id");
         }*/

        $scope.publishingType = function() {
            if (!$scope.isPublisherSelected()) {
                console.log("publishingType = 'NONE'");
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
            return $scope.publisher && $scope.publisher.id && $scope.itemValidated && $scope.classifications && $scope.classifications.length > 0
                && ($scope.publisher.context.reader.writingMode == "STATIC" || ($scope.targets && $scope.targets.length > 0));
        };

        $scope.canSave = function() {
            return $scope.publisher && $scope.publisher.id && $scope.itemValidated ; //&& $scope.classifications.length > 0 && $scope.targets.length > 0 ;
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
            console.log("publishing : " + JSON.stringify(content));
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

    })/*.filter("greaterThanToday", function () {
        return function (input) {
            var today = new Date();
            console.log("come into filter with input ", input);
            if (angular.isDate(input)) {
                console.log("input is a date, and today", today);
                if (new Date(input) > new Date(today)) {
                    console.log("input is upper of today");
                    return input;
                }
            }
            return today;
        }
    })*/;
