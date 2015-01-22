'use strict';

angular.module('publisherApp')
    .controller('PermissionOnContextDetailController', function ($scope, $stateParams, PermissionOnContext, Evaluator) {
        $scope.permissionOnContext = {};
        $scope.load = function (id) {
            PermissionOnContext.get({id: id}, function(result) {
              $scope.permissionOnContext = result;
            });
        };
        $scope.load($stateParams.id);
    });
