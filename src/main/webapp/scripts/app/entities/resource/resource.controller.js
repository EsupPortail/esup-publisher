'use strict';

angular.module('publisherApp')
    .controller('ResourceController', function ($scope, Resource, Organization, Redactor, ParseLinks) {
        $scope.resources = [];
        $scope.organizations = Organization.query();
        $scope.redactors = Redactor.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Resource.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.resources = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Resource.update($scope.resource,
                function () {
                    $scope.loadAll();
                    $('#saveResourceModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Resource.get({id: id}, function(result) {
                $scope.resource = result;
                $('#saveResourceModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Resource.get({id: id}, function(result) {
                $scope.resource = result;
                $('#deleteResourceConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Resource.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteResourceConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.resource = {title: null, enclosure: null, endDate: null, startDate: null, validatedBy: null, validatedDate: null, status: null, summary: null, ressourceUrl: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
