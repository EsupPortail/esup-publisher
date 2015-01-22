'use strict';

angular.module('publisherApp')
    .controller('RedactorDetailController', function ($scope, $stateParams, Redactor, Publisher) {
        $scope.redactor = {};
        $scope.load = function (id) {
            Redactor.get({id: id}, function(result) {
              $scope.redactor = result;
            });
        };
        $scope.load($stateParams.id);
    });
