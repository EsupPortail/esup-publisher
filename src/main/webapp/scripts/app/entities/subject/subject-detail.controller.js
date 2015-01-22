'use strict';

angular.module('publisherApp')
    .controller('SubjectDetailController', function ($scope, $stateParams, Subject) {
        $scope.subject = {};
        $scope.attributes = {};
        $scope.load = function (type, id) {
            Subject.getSubjectInfos({type: type, id: id}, function(result) {
              $scope.subject = result;
            });
            switch(type) {
                case "PERSON" :
                    Subject.getUserDisplayedAttrs(function(result) {
                        $scope.attributes = result;
                    });
                    break;
                case "GROUP" :
                    Subject.getGroupDisplayedAttrs(function(result) {
                        $scope.attributes = result;
                    });
                    break;
            }
        };
        $scope.load($stateParams.type, $stateParams.id);
    });
