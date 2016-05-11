'use strict';
angular.module('publisherApp')
    .controller('ClassificationChoiceController', function ($scope, $state, $filter, Classification, loadedClassifications) {

        $scope.$parent.classifications = $scope.$parent.classifications || [];
        $scope.$parent.targets = $scope.$parent.targets || [];

        $scope.classificationsList = [];

        //$scope.selectedClassifications = [];

        /*if ($scope.$parent.classifications.length > 0) {
            $scope.selectedClassifications = angular.copy($scope.$parent.classifications);
            console.log("selected calssif :", JSON.stringify($scope.selectedClassifications) );
        }*/

        if (angular.equals([],$scope.$parent.classifications) && loadedClassifications) {
            $scope.$parent.classifications = angular.copy(loadedClassifications);
            //console.log('loaded classifications :', $scope.$parent.classifications);
        }

        //TODO managing level of publication = 2 in "grouping" Feeds in categories name

        $scope.loadAll = function() {
            Classification.query({publisherId: $scope.$parent.publisher.id, isPublishing: true}, function(result) {
                //$scope.classificationsList = result;
                angular.forEach(result, function(obj) {
                    /*var ticked = false;
                    if (containsSelectedClassification(obj.contextKey)) {
                        ticked = true;
                    }*/
                    $scope.classificationsList.push({contextKey : obj.contextKey, icon : obj.iconUrl, name: obj.name, description : obj.description, color : obj.color});
                    //$scope.classificationsList.push({contextKey : obj.contextKey, icon : obj.iconUrl, name: obj.name, description : obj.description, ticked: ticked});
                });
                //console.log("after load already selected : ",$scope.$parent.classifications );
                //console.log("after load",$scope.classificationsList );
            });
        };

        $scope.goOnTargets = function() {
            //console.log("goOnTargets : " + JSON.stringify($scope.$parent.publisher.context.redactor.writingMode == "TARGETS_ON_ITEM"));
            return $scope.$parent.publisher.context.redactor.writingMode == "TARGETS_ON_ITEM";
        };

        $scope.loadAll();

        /*$scope.localLang = {
            selectAll       : $filter('translate')("global.form.multiSelect.selectAll"),
            selectNone      : $filter('translate')("global.form.multiSelect.selectNone"),
            reset           : $filter('translate')("global.form.multiSelect.reset"),
            search          : $filter('translate')("global.form.multiSelect.search"),
            nothingSelected : $filter('translate')("global.form.multiSelect.nothingSelected")
        };*/

        /*$scope.validateClassifications = function() {
            console.log("validate Classifications : ");
            $scope.$parent.classifications = [];
            angular.forEach($scope.selectedClassifications, function(obj) {
                //$scope.$parent.classifications.push(obj.contextKey);
                $scope.$parent.classifications.push(obj);
            });
            console.log("Select Classifications are : ", JSON.stringify($scope.$parent.classifications));
        };*/

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

        /*$scope.getBackgroundStyle = function(classification){
            if (classification.icon) {
                var style = "'background-image':'url(" + classification.icon + ")'";
                console.log("with background ", style);
                return style;
            }
            return "";
        };*/

    });
