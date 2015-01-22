'use strict';

angular.module('publisherApp')
    .controller('FilterController', function ($scope, Filter, EnumDatas, dataResolved) {
        $scope.filters = [];
        $scope.organizations = dataResolved[0];
        $scope.filterTypeList = EnumDatas.getFilterTypeList();

        $scope.loadAll = function() {
            Filter.query(function(result) {
               $scope.filters = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Filter.update($scope.filter,
                function () {
                    $scope.loadAll();
                    $('#saveFilterModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Filter.get({id: id}, function(result) {
                $scope.filter = result;
            	$('#saveFilterModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Filter.get({id: id}, function(result) {
                $scope.filter = result;
            	$('#deleteFilterConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Filter.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteFilterConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.filter = {pattern: null, description: null, type: $scope.filterTypeList[0], id: null, organization: $scope.organizations[0]};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
