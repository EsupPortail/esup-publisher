'use strict';

angular.module('publisherApp')
    .controller('AuthenticatedUserEvaluatorController', function ($scope, AuthenticatedUserEvaluator, ParseLinks) {
        $scope.authenticatedUserEvaluators = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            AuthenticatedUserEvaluator.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.authenticatedUserEvaluators = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            AuthenticatedUserEvaluator.update($scope.authenticatedUserEvaluator,
                function () {
                    $scope.loadAll();
                    $('#saveAuthenticatedUserEvaluatorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            AuthenticatedUserEvaluator.get({id: id}, function(result) {
                $scope.authenticatedUserEvaluator = result;
                $('#saveAuthenticatedUserEvaluatorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            AuthenticatedUserEvaluator.get({id: id}, function(result) {
                $scope.authenticatedUserEvaluator = result;
                $('#deleteAuthenticatedUserEvaluatorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            AuthenticatedUserEvaluator.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAuthenticatedUserEvaluatorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.authenticatedUserEvaluator = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
