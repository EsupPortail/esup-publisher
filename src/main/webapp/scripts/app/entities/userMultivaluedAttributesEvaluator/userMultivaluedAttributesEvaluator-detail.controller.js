'use strict';

angular.module('publisherApp')
    .controller('UserMultivaluedAttributesEvaluatorDetailController', function ($scope, $stateParams, UserMultivaluedAttributesEvaluator) {
        $scope.userMultivaluedAttributesEvaluator = {};
        $scope.load = function (id) {
            UserMultivaluedAttributesEvaluator.get({id: id}, function(result) {
              $scope.userMultivaluedAttributesEvaluator = result;
            });
        };
        $scope.load($stateParams.id);
    });
