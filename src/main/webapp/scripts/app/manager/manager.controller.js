'use strict';

angular.module('publisherApp')
    .controller('ManagerController', function ($scope, dataResolved, EnumDatas) {
        $scope.organizations = dataResolved[0];
        $scope.redactors = dataResolved[1];
        $scope.itemStateList = EnumDatas.getItemStatusList();
    });
