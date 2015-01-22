'use strict';

angular.module('publisherApp')
    .controller('PermissionOnContextController', function ($scope, PermissionOnContext, Evaluator, ParseLinks) {
        $scope.permissionOnContexts = [];
        $scope.evaluators = Evaluator.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            PermissionOnContext.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.permissionOnContexts = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            PermissionOnContext.update($scope.permissionOnContext,
                function () {
                    $scope.loadAll();
                    $('#savePermissionOnContextModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            PermissionOnContext.get({id: id}, function(result) {
                $scope.permissionOnContext = result;
                $('#savePermissionOnContextModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            PermissionOnContext.get({id: id}, function(result) {
                $scope.permissionOnContext = result;
                $('#deletePermissionOnContextConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            PermissionOnContext.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePermissionOnContextConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.permissionOnContext = {ctxId: null, ctxType: null, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
