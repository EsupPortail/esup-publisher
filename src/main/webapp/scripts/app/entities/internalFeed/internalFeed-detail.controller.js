'use strict';

angular.module('publisherApp')
    .controller('InternalFeedDetailController', function ($scope, $stateParams, InternalFeed, Publisher, Category) {
        $scope.internalFeed = {};
        $scope.load = function (id) {
            InternalFeed.get({id: id}, function(result) {
              $scope.internalFeed = result;
            });
        };
        $scope.load($stateParams.id);
    });
