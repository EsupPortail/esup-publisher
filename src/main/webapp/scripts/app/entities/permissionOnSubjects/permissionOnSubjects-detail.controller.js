'use strict';

angular.module('publisherApp')
    .controller('PermissionOnSubjectsDetailController', function ($scope, $stateParams, PermissionOnSubjects, Evaluator, SubjectPermKey) {
        $scope.permissionOnSubjects = {};
        $scope.load = function (id) {
            PermissionOnSubjects.get({id: id}, function(result) {
              $scope.permissionOnSubjects = result;
            });
        };
        $scope.load($stateParams.id);
    });
