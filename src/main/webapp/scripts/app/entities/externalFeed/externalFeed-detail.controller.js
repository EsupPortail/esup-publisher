'use strict';

angular.module('publisherApp')
    .controller('ExternalFeedDetailController', function ($scope, $stateParams, ExternalFeed, Publisher, Category) {
        $scope.externalFeed = {};
        $scope.load = function (id) {
            ExternalFeed.get({id: id}, function(result) {
              $scope.externalFeed = result;
            });
        };
        $scope.load($stateParams.id);
    });
