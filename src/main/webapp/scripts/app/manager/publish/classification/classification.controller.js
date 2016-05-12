'use strict';
angular.module('publisherApp')
    .controller('ClassificationChoiceController', function ($scope, $state, $filter, Classification, loadedClassifications) {

        $scope.$parent.classifications = $scope.$parent.classifications || [];
        $scope.$parent.targets = $scope.$parent.targets || [];

        $scope.classificationsList = [];

        if (angular.equals([],$scope.$parent.classifications) && loadedClassifications) {
            $scope.$parent.classifications = angular.copy(loadedClassifications);
        }

        //TODO managing level of publication = 2 in "grouping" Feeds in categories name

        $scope.loadAll = function() {
            Classification.query({publisherId: $scope.$parent.publisher.id, isPublishing: true}, function(result) {
                angular.forEach(result, function(obj) {
                    $scope.classificationsList.push({contextKey : obj.contextKey, icon : obj.iconUrl, name: obj.name, description : obj.description, color : obj.color});
                });
                // if only one Classification is enable automatic select it !
                if ($scope.classificationsList.length == 1) {
                    $scope.toggleSelection($scope.classificationsList[0].contextKey);
                }
            });
        };

        $scope.goOnTargets = function() {
            //console.log("goOnTargets : " + JSON.stringify($scope.$parent.publisher.context.redactor.writingMode == "TARGETS_ON_ITEM"));
            return $scope.$parent.publisher.context.redactor.writingMode == "TARGETS_ON_ITEM";
        };

        $scope.loadAll();

        $scope.toggleSelection = function (contextKey) {
            var i = 0, idx=-1;
            for (var size = $scope.$parent.classifications.length; i < size; i++) {
                //console.log("checking equals ", $scope.$parent.classifications[i], contextKey);
                if (angular.equals($scope.$parent.classifications[i], contextKey)) {
                    idx = i;
                    break;
                }
            }
            // is currently selected
            if (idx > -1) {
                $scope.$parent.classifications.splice(idx, 1);
            }
            // is newly selected
            else {
                $scope.$parent.classifications.push(contextKey);
            }
        };


        $scope.containsSelectedClassification = function (contextKey) {
            var list = $scope.$parent.classifications;
            if (list.length > 0) {
                for (var i = 0, size = list.length; i < size; i++) {
                    //console.log("checking equals ", list[i], contextKey);
                    if (angular.equals(list[i], contextKey)) {
                        return true;
                    }
                }
            }
            return false;
        };

    });
