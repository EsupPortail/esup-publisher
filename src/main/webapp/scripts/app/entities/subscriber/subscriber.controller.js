'use strict';

angular.module('publisherApp')
    .controller('SubscriberController', function ($scope, Subscriber, ParseLinks) {
        $scope.subscribers = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Subscriber.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.subscribers = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Subscriber.update($scope.subscriber,
                function () {
                    $scope.loadAll();
                    $('#saveSubscriberModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Subscriber.get({id: id}, function(result) {
                $scope.subscriber = result;
                $('#saveSubscriberModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Subscriber.get({id: id}, function(result) {
                $scope.subscriber = result;
                $('#deleteSubscriberConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Subscriber.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSubscriberConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.subscriber = {ctxId: null, ctxType: null, keyId: null, keyType: null, subscribeType: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
