'use strict';

angular.module('publisherApp')
    .controller('PermissionOnSubjectsWithClassificationListController', function ($scope, PermissionOnSubjectsWithClassificationList, Evaluator, SubjectPermKey, ContextKey, ParseLinks) {
        $scope.permissionOnSubjectsWithClassificationLists = [];
        $scope.evaluators = Evaluator.query();
        $scope.subjectpermkeys = SubjectPermKey.query();
        $scope.contextkeys = ContextKey.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            PermissionOnSubjectsWithClassificationList.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.permissionOnSubjectsWithClassificationLists = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            PermissionOnSubjectsWithClassificationList.update($scope.permissionOnSubjectsWithClassificationList,
                function () {
                    $scope.loadAll();
                    $('#savePermissionOnSubjectsWithClassificationListModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            PermissionOnSubjectsWithClassificationList.get({id: id}, function(result) {
                $scope.permissionOnSubjectsWithClassificationList = result;
                $('#savePermissionOnSubjectsWithClassificationListModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            PermissionOnSubjectsWithClassificationList.get({id: id}, function(result) {
                $scope.permissionOnSubjectsWithClassificationList = result;
                $('#deletePermissionOnSubjectsWithClassificationListConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            PermissionOnSubjectsWithClassificationList.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePermissionOnSubjectsWithClassificationListConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.permissionOnSubjectsWithClassificationList = {ctxId: null, ctxType: null, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
