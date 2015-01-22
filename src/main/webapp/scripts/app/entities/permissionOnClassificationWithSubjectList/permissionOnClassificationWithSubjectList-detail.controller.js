'use strict';

angular.module('publisherApp')
    .controller('PermissionOnClassificationWithSubjectListDetailController', function ($scope, $stateParams, PermissionOnClassificationWithSubjectList, Evaluator, SubjectKey) {
        $scope.permissionOnClassificationWithSubjectList = {};
        $scope.load = function (id) {
            PermissionOnClassificationWithSubjectList.get({id: id}, function(result) {
              $scope.permissionOnClassificationWithSubjectList = result;
            });
        };
        $scope.load($stateParams.id);
    });
