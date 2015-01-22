'use strict';

angular.module('publisherApp')
    .controller('OperatorEvaluatorDetailController', function ($scope, $stateParams, OperatorEvaluator, Evaluator) {
        $scope.operatorEvaluator = {};
        $scope.load = function (id) {
            OperatorEvaluator.get({id: id}, function(result) {
              $scope.operatorEvaluator = result;
            });
        };
        $scope.load($stateParams.id);
    });
