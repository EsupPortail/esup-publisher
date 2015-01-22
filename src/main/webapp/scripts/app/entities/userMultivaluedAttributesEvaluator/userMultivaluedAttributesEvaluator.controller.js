'use strict';

angular.module('publisherApp')
    .controller('UserMultivaluedAttributesEvaluatorController', function ($scope, UserMultivaluedAttributesEvaluator) {
        $scope.userMultivaluedAttributesEvaluators = [];
        $scope.loadAll = function() {
            UserMultivaluedAttributesEvaluator.query(function(result) {
               $scope.userMultivaluedAttributesEvaluators = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            UserMultivaluedAttributesEvaluator.update($scope.userMultivaluedAttributesEvaluator,
                function () {
                    $scope.loadAll();
                    $('#saveUserMultivaluedAttributesEvaluatorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            UserMultivaluedAttributesEvaluator.get({id: id}, function(result) {
                $scope.userMultivaluedAttributesEvaluator = result;
                $('#saveUserMultivaluedAttributesEvaluatorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            UserMultivaluedAttributesEvaluator.get({id: id}, function(result) {
                $scope.userMultivaluedAttributesEvaluator = result;
                $('#deleteUserMultivaluedAttributesEvaluatorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            UserMultivaluedAttributesEvaluator.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserMultivaluedAttributesEvaluatorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.userMultivaluedAttributesEvaluator = {mode: null, attribute: null, value: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
