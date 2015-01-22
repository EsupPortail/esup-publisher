'use strict';

angular.module('publisherApp')
    .controller('PermissionOnSubjectsController', function ($scope, PermissionOnSubjects, Evaluator, SubjectPermKey, ParseLinks) {
        $scope.permissionOnSubjectss = [];
        $scope.evaluators = Evaluator.query();
        $scope.subjectpermkeys = SubjectPermKey.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            PermissionOnSubjects.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.permissionOnSubjectss = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            PermissionOnSubjects.update($scope.permissionOnSubjects,
                function () {
                    $scope.loadAll();
                    $('#savePermissionOnSubjectsModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            PermissionOnSubjects.get({id: id}, function(result) {
                $scope.permissionOnSubjects = result;
                $('#savePermissionOnSubjectsModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            PermissionOnSubjects.get({id: id}, function(result) {
                $scope.permissionOnSubjects = result;
                $('#deletePermissionOnSubjectsConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            PermissionOnSubjects.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePermissionOnSubjectsConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.permissionOnSubjects = {ctxId: null, ctxType: null, role: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
