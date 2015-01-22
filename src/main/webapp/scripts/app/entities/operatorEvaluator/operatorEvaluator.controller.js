'use strict';

angular.module('publisherApp')
    .controller('OperatorEvaluatorController', function ($scope, OperatorEvaluator, Evaluator) {
        $scope.operatorEvaluators = [];
        $scope.evaluators = Evaluator.query();
        $scope.loadAll = function() {
            OperatorEvaluator.query(function(result) {
               $scope.operatorEvaluators = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            OperatorEvaluator.update($scope.operatorEvaluator,
                function () {
                    $scope.loadAll();
                    $('#saveOperatorEvaluatorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            OperatorEvaluator.get({id: id}, function(result) {
                $scope.operatorEvaluator = result;
                $('#saveOperatorEvaluatorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            OperatorEvaluator.get({id: id}, function(result) {
                $scope.operatorEvaluator = result;
                $('#deleteOperatorEvaluatorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            OperatorEvaluator.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOperatorEvaluatorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.operatorEvaluator = {type: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
