'use strict';

angular.module('publisherApp')
    .controller('AuthenticatedUserEvaluatorDetailController', function ($scope, $stateParams, AuthenticatedUserEvaluator) {
        $scope.authenticatedUserEvaluator = {};
        $scope.load = function (id) {
            AuthenticatedUserEvaluator.get({id: id}, function(result) {
              $scope.authenticatedUserEvaluator = result;
            });
        };
        $scope.load($stateParams.id);
    });
