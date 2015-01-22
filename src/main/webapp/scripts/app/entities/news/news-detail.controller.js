'use strict';

angular.module('publisherApp')
    .controller('NewsDetailController', function ($scope, $stateParams, News, Organization, Redactor) {
        $scope.news = {};
        $scope.load = function (id) {
            News.get({id: id}, function(result) {
              $scope.news = result;
            });
        };
        $scope.load($stateParams.id);
    });
