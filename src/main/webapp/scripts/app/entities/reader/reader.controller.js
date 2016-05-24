'use strict';

angular.module('publisherApp')
    .controller('ReaderController', function ($scope, Reader, EnumDatas) {
        $scope.readers = [];
        $scope.authorizedTypesList = EnumDatas.getItemTypeList();
        $scope.classificationDecorTypesList = EnumDatas.getClassificationDecorTypeList();
        $scope.authorizedTypesDirty = false;
        $scope.classificationDecorTypesDirty = false;

        $scope.loadAll = function() {
            Reader.query(function(result) {
               $scope.readers = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Reader.update($scope.reader,
                function () {
                    $scope.loadAll();
                    $('#saveReaderModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Reader.get({id: id}, function(result) {
                $scope.reader = result;
                $('#saveReaderModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Reader.get({id: id}, function(result) {
                $scope.reader = result;
                $('#deleteReaderConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Reader.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteReaderConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.reader = {name: null, displayName: null, description: null, id: null, authorizedTypes: [], classificationDecorations: []};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
            $scope.authorizedTypesDirty = false;
            $scope.classificationDecorTypesDirty = false;
        };

        $scope.containsSelectedType = function (type) {
            if (angular.isDefined($scope.reader)) {
                var list = $scope.reader.authorizedTypes || [];
                if (list.length > 0) {
                    for (var i = 0, size = list.length; i < size; i++) {
                        //console.log("checking equals ", list[i], type);
                        if (angular.equals(list[i], type)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        };
        $scope.containsSelectedDecorType = function (type) {
            if (angular.isDefined($scope.reader)) {
                var list = $scope.reader.classificationDecorations || [];
                if (list.length > 0) {
                    for (var i = 0, size = list.length; i < size; i++) {
                        //console.log("checking equals ", list[i], type);
                        if (angular.equals(list[i], type)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        };

        $scope.toggleSelectionType = function (type) {
            $scope.authorizedTypesDirty = true;
            var i = 0, idx=-1;
            for (var size = $scope.reader.authorizedTypes.length; i < size; i++) {
                //console.log("checking equals ", $scope.reader.authorizedTypes[i], type);
                if (angular.equals($scope.reader.authorizedTypes[i], type)) {
                    idx = i;
                    break;
                }
            }
            // is currently selected
            if (idx > -1) {
                $scope.reader.authorizedTypes.splice(idx, 1);
            }
            // is newly selected
            else {
                $scope.reader.authorizedTypes.push(type);
            }
        };


        $scope.toggleSelectionDecorType = function (type) {
            $scope.classificationDecorTypesDirty = true;
            var i = 0, idx=-1;
            for (var size = $scope.reader.classificationDecorations.length; i < size; i++) {
                //console.log("checking equals ", $scope.reader.authorizedTypes[i], type);
                if (angular.equals($scope.reader.classificationDecorations[i], type)) {
                    idx = i;
                    break;
                }
            }
            // is currently selected
            if (idx > -1) {
                $scope.reader.classificationDecorations.splice(idx, 1);
            }
            // is newly selected
            else {
                $scope.reader.classificationDecorations.push(type);
            }
        };
    });
