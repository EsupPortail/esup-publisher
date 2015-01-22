'use strict';

angular.module('publisherApp')
    .controller('UserGroupEvaluatorController', function ($scope, UserGroupEvaluator, ParseLinks) {
        $scope.userGroupEvaluators = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            UserGroupEvaluator.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.userGroupEvaluators = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            UserGroupEvaluator.update($scope.userGroupEvaluator,
                function () {
                    $scope.loadAll();
                    $('#saveUserGroupEvaluatorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            UserGroupEvaluator.get({id: id}, function(result) {
                $scope.userGroupEvaluator = result;
                $('#saveUserGroupEvaluatorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            UserGroupEvaluator.get({id: id}, function(result) {
                $scope.userGroupEvaluator = result;
                $('#deleteUserGroupEvaluatorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            UserGroupEvaluator.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserGroupEvaluatorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.userGroupEvaluator = {group: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
