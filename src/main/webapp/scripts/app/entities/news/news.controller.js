'use strict';

angular.module('publisherApp')
    .controller('NewsController', function ($scope, News, Organization, Redactor, ParseLinks) {
        $scope.newss = [];
        $scope.organizations = Organization.query();
        $scope.redactors = Redactor.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            News.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.newss = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            News.update($scope.news,
                function () {
                    $scope.loadAll();
                    $('#saveNewsModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            News.get({id: id}, function(result) {
                $scope.news = result;
                $('#saveNewsModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            News.get({id: id}, function(result) {
                $scope.news = result;
                $('#deleteNewsConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            News.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteNewsConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.news = {title: null, enclosure: null, endDate: null, startDate: null, validatedBy: null, validatedDate: null, status: null, summary: null, body: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
