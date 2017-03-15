'use strict';

angular.module('publisherApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal, User) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.isInRole = Principal.isInRole;
        $scope.$state = $state;

        $scope.canModerate = false;

        $scope.load = function() {
            User.canModerateAnyThing(function (data) {
                $scope.canModerate = (data.value == true);
            });
        };
        $scope.load();

        $scope.isCollapsed = false;

        $scope.collapse = function() {
            $scope.isCollapsed = false;
        };

        $scope.$on('$stateChangeSuccess', function (event, next) {
            $scope.isCollapsed = true;
        });

        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };

        $scope.login = function () {
            Auth.login();
            $state.go('home');
        };
    });
