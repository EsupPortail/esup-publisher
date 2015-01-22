'use strict';

angular.module('publisherApp')
    .controller('CategoryDetailController', function ($scope, $stateParams, Category, Publisher) {
        $scope.category = {};
        $scope.load = function (id) {
            Category.get({id: id}, function(result) {
              $scope.category = result;
            });
        };
        $scope.load($stateParams.id);
    });
