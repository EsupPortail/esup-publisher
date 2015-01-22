'use strict';

angular.module('publisherApp')
    .controller('MediaController', function ($scope, Media, Organization, Redactor, ParseLinks) {
        $scope.medias = [];
        $scope.organizations = Organization.query();
        $scope.redactors = Redactor.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Media.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.medias = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Media.update($scope.media,
                function () {
                    $scope.loadAll();
                    $('#saveMediaModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Media.get({id: id}, function(result) {
                $scope.media = result;
                $('#saveMediaModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Media.get({id: id}, function(result) {
                $scope.media = result;
                $('#deleteMediaConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Media.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMediaConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.media = {title: null, enclosure: null, endDate: null, startDate: null, validatedBy: null, validatedDate: null, status: null, summary: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
