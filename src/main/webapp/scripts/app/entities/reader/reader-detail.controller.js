'use strict';

angular.module('publisherApp')
    .controller('ReaderDetailController', function ($scope, $stateParams, Reader) {
        $scope.reader = {};
        $scope.load = function (id) {
            Reader.get({id: id}, function(result) {
              $scope.reader = result;
            });
        };
        $scope.load($stateParams.id);
    });
