'use strict';

angular.module('publisherApp')
    .controller('TargetsPublishController', function ($scope, $state, $filter, Subject, Group, User, loadedTargets) {
        $scope.$parent.targets = $scope.$parent.targets || [];

        $scope.selectedtargets = $scope.$parent.targets;

        $scope.search = {target: {}};



        if (angular.equals([],$scope.$parent.targets) && loadedTargets) {
            //console.log('loaded targets :' + JSON.stringify(loadedTargets));
            // on récupère le subject car pas utile de mettre le subscribeType car FORCED par défaut
            if (loadedTargets.length > 0) {
                for (var i = 0; i < loadedTargets.length; ++i) {
                    $scope.$parent.targets.push(angular.copy(loadedTargets[i].subject));
                }
            }
        }

        $scope.$watch('search.target', function(newData, oldData) {
            console.log("search selected :", newData, oldData);
            if (angular.isDefined(newData) && !angular.equals({},newData) && newData.hasOwnProperty('modelId') && !containsSubcriber(newData.modelId)) {
                var target = {modelId :newData.modelId, displayName: newData.displayName};
                $scope.$parent.targets.push(target);

            }
        });

        $scope.remove = function(subject) {
            $scope.$parent.targets = $scope.$parent.targets.filter(function(element) {
                return !angular.equals(element.modelId, subject.modelId);
            });
        };

        $scope.viewSubject= function(subject) {
            if (subject.modelId) {
                $state.go('publish.targets.subject', subject.modelId);
            }
            return false;
        };

        function containsSubcriber (subjectKey) {
            var list = $scope.$parent.targets;
            if (list.length > 0) {
                for (var i = 0, size = list.length; i < size; i++) {
                    //console.log("checking equals ", list[i], subjectKey);
                    if (angular.equals(list[i].modelId, subjectKey)) {
                        return true;
                    }
                }
            }
            return false;
        }
    });

