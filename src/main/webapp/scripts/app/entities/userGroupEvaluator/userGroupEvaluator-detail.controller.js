'use strict';

angular.module('publisherApp')
    .controller('UserGroupEvaluatorDetailController', function ($scope, $stateParams, UserGroupEvaluator) {
        $scope.userGroupEvaluator = {};
        $scope.load = function (id) {
            UserGroupEvaluator.get({id: id}, function(result) {
              $scope.userGroupEvaluator = result;
            });
        };
        $scope.load($stateParams.id);
    });
