'use strict';

angular.module('publisherApp')
    .controller('SubscriberDetailController', function ($scope, $stateParams, Subscriber) {
        $scope.subscriber = {};
        $scope.load = function (id) {
            Subscriber.get({id: id}, function(result) {
              $scope.subscriber = result;
            });
        };
        $scope.load($stateParams.id);
    });
