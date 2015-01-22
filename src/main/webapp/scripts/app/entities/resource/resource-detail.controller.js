'use strict';

angular.module('publisherApp')
    .controller('ResourceDetailController', function ($scope, $stateParams, Resource, Organization, Redactor) {
        $scope.resource = {};
        $scope.load = function (id) {
            Resource.get({id: id}, function(result) {
              $scope.resource = result;
            });
        };
        $scope.load($stateParams.id);
    });
