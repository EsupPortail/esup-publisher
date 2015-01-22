'use strict';

angular.module('publisherApp')
    .controller('PublisherDetailController', function ($scope, $stateParams, Publisher) {
        $scope.publisher = {};
        $scope.load = function (id) {
            Publisher.get({id: id}, function(result) {
              $scope.publisher = result;
            });
        };
        $scope.load($stateParams.id);
    });
