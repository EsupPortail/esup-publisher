'use strict';

angular.module('publisherApp')
    .controller('InternalFeedController', function ($scope, InternalFeed, Publisher, Category) {
        $scope.internalFeeds = [];
        $scope.publishers = Publisher.query();
        $scope.categorys = Category.query();
        $scope.loadAll = function() {
            InternalFeed.query(function(result) {
               $scope.internalFeeds = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            InternalFeed.update($scope.internalFeed,
                function () {
                    $scope.loadAll();
                    $('#saveInternalFeedModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            InternalFeed.get({id: id}, function(result) {
                $scope.internalFeed = result;
                $('#saveInternalFeedModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            InternalFeed.get({id: id}, function(result) {
                $scope.internalFeed = result;
                $('#deleteInternalFeedConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            InternalFeed.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteInternalFeedConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.internalFeed = {rssAllowed: null, name: null, iconUrl: null, lang: null, ttl: null, displayOrder: null, accessView: null, description: null, defaultDisplayOrder: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
