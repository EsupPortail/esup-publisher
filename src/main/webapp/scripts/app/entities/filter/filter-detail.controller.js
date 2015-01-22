'use strict';

angular.module('publisherApp')
    .controller('FilterDetailController', function ($scope, $stateParams, Filter, Organization) {
        $scope.filter = {};
        $scope.load = function (id) {
            Filter.get({id: id}, function(result) {
              $scope.filter = result;
            });
        };
        $scope.load($stateParams.id);
    });
