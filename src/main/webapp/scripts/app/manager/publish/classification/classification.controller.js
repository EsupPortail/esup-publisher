'use strict';
angular.module('publisherApp')
    .controller('ClassificationChoiceController', function ($scope, $state, $filter, Classification, loadedClassifications) {

        $scope.$parent.classifications = $scope.$parent.classifications || [];
        $scope.$parent.targets = $scope.$parent.targets || [];
        $scope.$parent.classificationsValidated = $scope.$parent.classificationsValidated || false;

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
                // we clean already saved classification where user loose rights
                if (loadedClassifications) {
                    angular.forEach(loadedClassifications, function(obj) {
                        var found = false;
                        angular.forEach($scope.classificationsList, function(objList) {
                            if (angular.equals(objList.contextKey, obj)) {
                                found = true;
                            }
                        });
                        if (!found) {
                            remove(obj);
                        }
                    });
                }
                // if only one Classification is enable automatic select it !
                if ($scope.classificationsList.length == 1 && !inArray($scope.classificationsList[0].contextKey, $scope.$parent.classifications)) {
                    $scope.$parent.classifications.push($scope.classificationsList[0].contextKey);
                }
            });
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
            return inArray(contextKey, $scope.$parent.classifications);
        };

        $scope.validateClassifications = function (){
            $scope.$parent.classificationsValidated = true;
        };

        function remove(contextkey) {
            $scope.$parent.classifications = $scope.$parent.classifications.filter(function(element) {
                return !angular.equals(element, contextkey);
            });
        }

        function inArray (item, array) {
            //console.log("inArray", array, item);
            if (!angular.isDefined(array) || !angular.isArray(array) || array.length < 1) return false;
            for (var i = 0, size = array.length; i < size; i++) {
                if (angular.equals(array[i], item)) {
                    //console.log("inArray return true !");
                    return true;
                }
            }
            //console.log("inArray returned false !");
            return false;
        }

    });
