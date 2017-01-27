'use strict';

angular.module('publisherApp')
    .controller('RedactorDetailController', function ($scope, $stateParams, Redactor, EnumDatas) {
        $scope.redactor = {};
        $scope.writingModeList = EnumDatas.getWritingModeList();
        $scope.writingFormatList = EnumDatas.getWritingFormatList();

        $scope.load = function (id) {
            Redactor.get({id: id}, function(result) {
              $scope.redactor = result;
            });
        };
        $scope.load($stateParams.id);

        $scope.getWritingModeLabel = function(name) {
            return $scope.writingModeList.filter(function(val) {
                return val.name === name;
            })[0].label;
        };
    });
