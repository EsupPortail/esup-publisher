'use strict';
angular.module('publisherApp')
    .controller('ManagedController', function ($scope, Item, ContentDTO, ParseLinks) {
        $scope.items = [];
        $scope.page = 1;
        /** in manager state resolve
        $scope.itemStateList = itemStatusList;
        $scope.organizations = organizationList;
        $scope.redactors = redactorList;*/
        $scope.itemState = getEnumKey('PENDING');
        //$scope.itemStateList = ItemStatusList;//Enums.ItemStatus;
        $scope.itemStateForManager = $scope.itemStateList.filter(function(val) {
            return val.name !=  'DRAFT';
        });
        $scope.loadAll = function() {
            Item.query({page: $scope.page, per_page: 20, item_status: $scope.itemState}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.items = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.validate = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#validateItemConfirmation').modal('show');
            });
        };

        $scope.confirmValidate = function (id) {
            Item.patch({objectId:id, attribute : "validate", value: "true"},
                function () {
                    $scope.loadAll();
                    $('#validateItemConfirmation').modal('hide');
                    //$scope.clear();
                });
        };

        $scope.invalidate = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#inValidateItemConfirmation').modal('show');
            });
        };

        $scope.confirmInValidate = function (id) {
            Item.patch({objectId:id, attribute : "validate", value: "false"},
                function () {
                    $scope.loadAll();
                    $('#inValidateItemConfirmation').modal('hide');
                    //$scope.clear();
                });
        };
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

        $scope.onClickState = function (state) {
            $scope.itemState = state.id;
            $scope.loadAll();
        };

        $scope.isActiveState = function(stateId) {
            return stateId == $scope.itemState;
        };

        function getEnumKey (name) {
            return $scope.itemStateList.filter(function(val) {
                return val.name === name;
            })[0].id;
        }
    });

