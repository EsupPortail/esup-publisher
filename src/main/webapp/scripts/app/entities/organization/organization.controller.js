'use strict';

angular.module('publisherApp')
    .controller('OrganizationController', function ($scope, Organization, ParseLinks) {
        $scope.organizations = [];
        //$scope.publishers = Publisher.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Organization.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.organizations.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.organizations = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            $scope.organization.displayName = $scope.organization.name;
            Organization.update($scope.organization,
                function () {
                    $scope.reset();
                    $('#saveOrganizationModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Organization.get({id: id}, function(result) {
                $scope.organization = result;
                $('#saveOrganizationModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Organization.get({id: id}, function(result) {
                $scope.organization = result;
                $('#deleteOrganizationConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Organization.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteOrganizationConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.organization = {name: null, displayName: null, description: null, displayOrder: 0, allowNotifications: false, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
