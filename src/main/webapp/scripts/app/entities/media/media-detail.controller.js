'use strict';

angular.module('publisherApp')
    .controller('MediaDetailController', function ($scope, $stateParams, Media, Organization, Redactor) {
        $scope.media = {};
        $scope.load = function (id) {
            Media.get({id: id}, function(result) {
              $scope.media = result;
            });
        };
        $scope.load($stateParams.id);
    });
