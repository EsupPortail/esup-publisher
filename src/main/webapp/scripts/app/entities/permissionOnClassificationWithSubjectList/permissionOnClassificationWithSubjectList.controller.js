'use strict';

angular.module('publisherApp')
    .controller('PermissionOnClassificationWithSubjectListController', function ($scope, PermissionOnClassificationWithSubjectList, Evaluator, SubjectKey, ParseLinks) {
        $scope.permissionOnClassificationWithSubjectLists = [];
        $scope.evaluators = Evaluator.query();
        $scope.subjectkeys = SubjectKey.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            PermissionOnClassificationWithSubjectList.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.permissionOnClassificationWithSubjectLists = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            PermissionOnClassificationWithSubjectList.update($scope.permissionOnClassificationWithSubjectList,
                function () {
                    $scope.loadAll();
                    $('#savePermissionOnClassificationWithSubjectListModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            PermissionOnClassificationWithSubjectList.get({id: id}, function(result) {
                $scope.permissionOnClassificationWithSubjectList = result;
                $('#savePermissionOnClassificationWithSubjectListModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            PermissionOnClassificationWithSubjectList.get({id: id}, function(result) {
                $scope.permissionOnClassificationWithSubjectList = result;
                $('#deletePermissionOnClassificationWithSubjectListConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            PermissionOnClassificationWithSubjectList.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePermissionOnClassificationWithSubjectListConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.permissionOnClassificationWithSubjectList = {ctxId: null, ctxType: null, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
