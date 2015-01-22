'use strict';

angular.module('publisherApp')
    .controller('UserAttributesEvaluatorController', function ($scope, UserAttributesEvaluator) {
        $scope.userAttributesEvaluators = [];
        $scope.loadAll = function() {
            UserAttributesEvaluator.query(function(result) {
               $scope.userAttributesEvaluators = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            UserAttributesEvaluator.update($scope.userAttributesEvaluator,
                function () {
                    $scope.loadAll();
                    $('#saveUserAttributesEvaluatorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            UserAttributesEvaluator.get({id: id}, function(result) {
                $scope.userAttributesEvaluator = result;
                $('#saveUserAttributesEvaluatorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            UserAttributesEvaluator.get({id: id}, function(result) {
                $scope.userAttributesEvaluator = result;
                $('#deleteUserAttributesEvaluatorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            UserAttributesEvaluator.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserAttributesEvaluatorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.userAttributesEvaluator = {mode: null, attribute: null, value: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
