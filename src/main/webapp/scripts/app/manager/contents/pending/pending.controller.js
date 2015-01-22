'use strict';
angular.module('publisherApp')
    .controller('PendingController', function ($scope, Item, ParseLinks) {
        $scope.items = [];
        $scope.page = 1;
        /** in manager state resolve
         $scope.itemStateList = itemStatusList;
         $scope.organizations = organizationList;
         $scope.redactors = redactorList;*/
        $scope.loadAll = function() {
            Item.query({page: $scope.page, per_page: 20, owned: false, item_status: getEnumKey('PENDING')}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.items = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


       /* $scope.update = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#saveItemModal').modal('show');
            });
        };*/

        $scope.delete = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#deleteItemConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ContentDTO.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteItemConfirmation').modal('hide');
                    //$scope.clear();
                });
        };

        /*$scope.clear = function () {
            $scope.item = {title: null, enclosure: null, endDate: null, startDate: null, validatedBy: null, validatedDate: null, status: null, summary: null, body: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };*/

        function getEnumKey (name) {
            return $scope.itemStateList.filter(function(val) {
                return val.name === name;
            })[0].id;
        }

    });

