'use strict';

angular.module('publisherApp')
    .controller('UserAttributesEvaluatorDetailController', function ($scope, $stateParams, UserAttributesEvaluator) {
        $scope.userAttributesEvaluator = {};
        $scope.load = function (id) {
            UserAttributesEvaluator.get({id: id}, function(result) {
              $scope.userAttributesEvaluator = result;
            });
        };
        $scope.load($stateParams.id);
    });
