'use strict';

angular.module('publisherApp')
    .controller('RedactorController', function ($scope, Redactor, EnumDatas) {
        $scope.redactors = [];
        $scope.writingModeList = EnumDatas.getWritingModeList();
        $scope.writingFormatList = EnumDatas.getWritingFormatList();

        $scope.loadAll = function() {
            Redactor.query(function(result) {
               $scope.redactors = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Redactor.update($scope.redactor,
                function () {
                    $scope.loadAll();
                    $('#saveRedactorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Redactor.get({id: id}, function(result) {
                $scope.redactor = result;
                $('#saveRedactorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Redactor.get({id: id}, function(result) {
                $scope.redactor = result;
                $('#deleteRedactorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Redactor.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteRedactorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.redactor = {name: null, displayName: null, description: null, format: $scope.writingFormatList[0], writingMode: $scope.writingModeList[0].name, nbLevelsOfClassification: 1, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };

        $scope.getWritingModeLabel = function(name) {
            return $scope.writingModeList.filter(function(val) {
                return val.name === name;
            })[0].label;
        };
    });
