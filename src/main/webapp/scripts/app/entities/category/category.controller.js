'use strict';

angular.module('publisherApp')
    .controller('CategoryController', function ($scope, Category, EnumDatas, dataResolved) {
        $scope.categorys = [];
        $scope.organizations = dataResolved[0];
        $scope.publishers = dataResolved[1];
        $scope.displayOrderTypeList = EnumDatas.getDisplayOrderTypeList();
        $scope.loadAll = function() {
            Category.query(function(result) {
               $scope.categorys = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Category.update($scope.category,
                function () {
                    $scope.loadAll();
                    $('#saveCategoryModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
                $('#saveCategoryModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
                $('#deleteCategoryConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Category.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCategoryConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.category = {rssAllowed: null, name: null, iconUrl: null, lang: null, ttl: null, displayOrder: null, accessView: null, description: null, defaultDisplayOrder: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
