'use strict';

angular.module('publisherApp')
    .controller('MainTreeViewController', function ($scope, $state, Publisher, Classification, Item) {
        $scope.treeModel = [];

        $scope.treeCore = {
            multiple: false,  // disable multiple node selection

            check_callback:true
        };

        $scope.currentNode = {};

        $scope.typesConfig = {
            "ORGANIZATION": {
                "icon": "fa fa-home fa-lg"
            },
            "PUBLISHER": {
                "icon": "fa fa-briefcase fa-lg"
            },
            "CATEGORY": {
                "icon": "fa fa-folder fa-lg"
            },
            "FEED": {
                "icon": "fa fa-rss fa-lg"
            },
            "ITEM": {
                "icon": "fa fa-newspaper-o fa-lg"
            }
        };

        $scope.nodeSelected = function(e, data) {
            console.log("Select node : " + JSON.stringify(data.node));
            var _l = data.node.id.split(":");
            $scope.currentNode = data.node;

            $state.go('ctxDetails', {ctxId: _l[0], ctxType: _l[1]});
        };
    });
