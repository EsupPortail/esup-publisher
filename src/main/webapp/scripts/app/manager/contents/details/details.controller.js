'use strict';

angular.module('publisherApp')
    .controller('ContentDetailsController', function ($scope, $state, EnumDatas, contentData) {
//console.log("enter ContentDetailsController",contentData);
        $scope.item = contentData[1].item;
        $scope.classifications = contentData[0];
        $scope.targets = contentData[1].targets;
        //console.log("ContentDetailsController", $scope.item, $scope.classifications,$scope.targets);
        $scope.pubContexts = [];

        if ($scope.classifications && $scope.classifications.length > 0) {
            angular.forEach($scope.classifications, function(value, key) {
                var found = false;
                angular.forEach($scope.pubContexts, function(v, k) {
                    if (v.id === value.publisher.id) {
                        $scope.pubContexts[k].classifications.push(value);
                        found = true;
                    }
                });
                if (!found) {
                    $scope.pubContexts.push({id: value.publisher.id, publisher: value.publisher, classifications: [value]});
                }
            });
            //console.log("Constructed map : ", $scope.pubContexts);
        }

        $scope.itemStatusList = EnumDatas.getItemStatusList();
        $scope.subscribeTypeList = EnumDatas.getSubscribeTypeList();
        $scope.subjectTypeList = EnumDatas.getSubjectTypeList();

        $scope.contentTemplate = '';

        $scope.activeNav = 'content';

        $scope.open = false;

        switch ($scope.item.type) {
            case 'NEWS':
                $scope.contentTemplate = 'scripts/app/manager/contents/details/templates/news.html';
                break;
            case 'MEDIA':
                $scope.contentTemplate = 'scripts/app/manager/contents/details/templates/media.html';
                break;
            case 'RESOURCE':
                $scope.contentTemplate = 'scripts/app/manager/contents/details/templates/resource.html';
                break;
            case 'FLASH':
                $scope.contentTemplate = 'scripts/app/manager/contents/details/templates/flash.html';
                break;
        }

        $scope.showInfo = function(){
            $scope.open = !$scope.open;
        };

        $scope.showNav = function(div){
            $scope.activeNav = div;
        };

        $scope.getSubscribeTypeLabel = function (name) {
            return $scope.subscribeTypeList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };
        $scope.getSubjectTypeLabel = function (name) {
            return $scope.subjectTypeList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };
        $scope.getItemTypeLabel = function (name) {
            return $scope.itemStatusList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };

        $scope.viewSubject= function(subject) {
            if (subject.subject.modelId) {
                $state.go('details.subject', subject.subject.modelId);
            }
            return false;
        };
    });
