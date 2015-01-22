'use strict';

angular.module('publisherApp')
    .controller('PermissionOnSubjectsWithClassificationListDetailController', function ($scope, $stateParams, PermissionOnSubjectsWithClassificationList, Evaluator, SubjectPermKey, ContextKey) {
        $scope.permissionOnSubjectsWithClassificationList = {};
        $scope.load = function (id) {
            PermissionOnSubjectsWithClassificationList.get({id: id}, function(result) {
              $scope.permissionOnSubjectsWithClassificationList = result;
            });
        };
        $scope.load($stateParams.id);
    });
