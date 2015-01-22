'use strict';

angular.module('publisherApp')
    .controller('ExternalFeedController', function ($scope, ExternalFeed, Publisher, Category) {
        $scope.externalFeeds = [];
        $scope.publishers = Publisher.query();
        $scope.categorys = Category.query();
        $scope.loadAll = function() {
            ExternalFeed.query(function(result) {
               $scope.externalFeeds = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            ExternalFeed.update($scope.externalFeed,
                function () {
                    $scope.loadAll();
                    $('#saveExternalFeedModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            ExternalFeed.get({id: id}, function(result) {
                $scope.externalFeed = result;
                $('#saveExternalFeedModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            ExternalFeed.get({id: id}, function(result) {
                $scope.externalFeed = result;
                $('#deleteExternalFeedConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ExternalFeed.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteExternalFeedConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.externalFeed = {rssAllowed: null, name: null, iconUrl: null, lang: null, ttl: null, displayOrder: null, accessView: null, description: null, defaultDisplayOrder: null, rssUrl: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
