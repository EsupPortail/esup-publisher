'use strict';

angular.module('publisherApp')
    .controller('PublisherController', function ($scope, Publisher, EnumDatas, dataResolved) {
        //console.log("callPublisherController ");
        $scope.publishers = [];
        $scope.organizations = dataResolved[0];
        $scope.redactors = dataResolved[1];
        $scope.readers = dataResolved[2];
        $scope.permissionClassList = EnumDatas.getPermissionClassList();
        $scope.displayOrderTypeList = EnumDatas.getDisplayOrderTypeList();

        $scope.loadAll = function() {
            Publisher.query(function(result) {
               $scope.publishers = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Publisher.update($scope.publisher,
                function () {
                    $scope.loadAll();
                    $('#savePublisherModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Publisher.get({id: id}, function(result) {
                $scope.publisher = result;
                $('#savePublisherModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Publisher.get({id: id}, function(result) {
                $scope.publisher = result;
                $('#deletePublisherConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Publisher.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePublisherConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            var organization, redactor, reader;
            organization = $scope.organizations[0];
            redactor = $scope.redactors[0];
            reader = $scope.readers[0];
            if (angular.isObject($scope.currentOrganization)) {
                organization = $scope.currentOrganization.id;
            }
            if (angular.isObject($scope.currentReader)) {
                redactor = $scope.currentReader.id;
            }
            if (angular.isObject($scope.currentRedactor)) {
                redactor = $scope.currentRedactor.id;
            }
            $scope.publisher = {
                context: {organization: organization, redactor: redactor, reader: reader},
                used: false, displayOrder: 0, permissionType: $scope.permissionClassList[0].name, defaultDisplayOrder: $scope.displayOrderTypeList[0].name, id: null,
                hasSubPermsManagement: true
            };
            //console.log($scope.publisher);
            if ($scope.editForm) {
                $scope.editForm.$setPristine();
                $scope.editForm.$setUntouched();
            }
        };


        $scope.getPermissionClassLabel = function(name) {
            return getEnumlabel('permissionClass', name);
        };

        $scope.getDisplayOrderTypeLabel = function(name) {
            return getEnumlabel('displayOrderType', name);
        };

        function getEnumlabel (type, name) {
            switch(type) {
                case 'permissionClass':
                    return $scope.permissionClassList.filter(function(val) {
                        return val.name === name;
                    })[0].label;
                    break;
                case 'displayOrderType':
                    return $scope.displayOrderTypeList.filter(function(val) {
                        return val.name === name;
                    })[0].label;
                    break;
            }
        }
    });
