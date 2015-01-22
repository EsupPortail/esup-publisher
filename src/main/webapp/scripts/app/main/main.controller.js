'use strict';

angular.module('publisherApp')
    .controller('MainController', function ($scope, Principal) {
        Principal.identity().then(function(account) {
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.account = account;
        })
    });
