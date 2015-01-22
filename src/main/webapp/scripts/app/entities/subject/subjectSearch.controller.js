'use strict';

angular.module('publisherApp')
    .controller("SubjectSearchController", function ($scope, $filter, User, Subject, Group ) {
        console.log("init SubjectControler : ", $scope.subjectType, $scope.subjectId, $scope.contextKey, $scope.type);

        $scope.userAttrs = Subject.getUserDisplayedAttrs();
        $scope.userResult = [];
        $scope.resultsArr = [];
        $scope.type = "";

        $scope.currentPage = 1;
        $scope.numPerPage = 10;
        $scope.listNumPerPage = [5,10,25,50,100];
        $scope.maxSize = 5;

        $scope.container = {subject: {}};

        $scope.search = {queryUserTerm: '', filter: ''};

        $scope.treeCoreGroups = {
            multiple: false,  // disable multiple node selection
            check_callback:true
        };
        $scope.treeCoreUsersFG = {
            multiple: false,  // disable multiple node selection
            check_callback:true
        };
        $scope.treeCheckboxConf= {
            three_state: false
        };

        if ($scope.searchType && $scope.searchType == 'GROUP') {
            $scope.type = "GROUP";
        }

        $scope.searchUser = function() {
            console.log("searchUser",$scope.search);
            $scope.resultsArr = [];
            $scope.userResult = [];
            User.search({ctx_id: $scope.contextKey.keyId, ctx_type: $scope.contextKey.keyType, criteria: $scope.search.queryUserTerm}, function(result) {
                if (result.length > 0) {
                    //console.log("Array has length : ", result.length);
                    $scope.userResult = $filter('orderBy')(result, 'displayName');
                    //console.log("result : ", $scope.userResult);
                    $scope.resultsArr = $scope.userResult.slice(0, $scope.numPerPage);
                    //console.log("results paginated : ", $scope.resultsArr);
                }
            });

        };

        $scope.getUserOfGroup = function(e, data) {
            console.log("getUserOfGroup",$scope.data);
            $scope.resultsArr = [];
            $scope.userResult = [];
            Group.userMembers({id: data.node.a_attr.model.modelId.keyId}, function(result) {
                if (result.length > 0) {
                    //console.log("Array has length : ", result.length);
                    $scope.userResult = $filter('orderBy')(result, 'displayName');
                    $scope.resultsArr = $scope.userResult.slice(0, $scope.numPerPage);
                }
            });

        };

        $scope.$watch('currentPage + numPerPage', function() {
            var begin = (($scope.currentPage - 1) * $scope.numPerPage)
                , end = begin + $scope.numPerPage;
            $scope.resultsArr = $scope.userResult.slice(begin, end);
        }, true);

        $scope.$watch('type', function(newData, oldData) {
            $scope.search = {queryUserTerm: '', filter:''};
            $scope.resultsArr = [];
            $scope.userResult = [];
            $scope.container.subject = {};
            console.log("type changed", $scope.type);
            if ($scope.searchUserForm) {
                $scope.searchUserForm.$setPristine();
                $scope.searchUserForm.$setUntouched();
            }
        }, false);

        $scope.$watch('container.subject', function() {
            console.log("subject controller", $scope.container.subject);
        });

        $scope.tooltipUser = function(user) {
            //console.log("user :", user);
            if (!angular.isDefined($scope.userAttrs) || !angular.isDefined(user) || !angular.isDefined(user.attributes)) return;
            var index;
            var attrs = user.attributes;
            var html = '';
            for (index = 0; index < $scope.userAttrs.length; ++index){
                if (index > 0 && angular.isDefined(attrs[$scope.userAttrs[index]])) {
                    html += " - ";
                }
                if (angular.isDefined(attrs[$scope.userAttrs[index]]) && angular.isArray(attrs[$scope.userAttrs[index]])) {
                    var subIndex;
                    for (subIndex = 0; subIndex < attrs[$scope.userAttrs[index]].length; ++subIndex){
                        if (subIndex > 0) {
                            html += ", ";
                        }
                        html += attrs[$scope.userAttrs[index]][subIndex];
                    }
                } else if (angular.isDefined(attrs[$scope.userAttrs[index]])) {
                    html +=  attrs[$scope.userAttrs[index]];
                }
            }
            return html;
        };

        $scope.checkGroup = function(e, data) {
            $scope.container.subject = data.node.a_attr.model;
            console.log("Selected subject : ",$scope.container.subject);
            $scope.$apply();
        };

        $scope.uncheckGroup = function(e, data) {
            $scope.container.subject = {};
            console.log("Unselect subject : ",$scope.container.subject);
            $scope.$apply();
        };

        $scope.submitSubject = function() {
            console.log("Selected subject : ",$scope.container.subject);
            if ($scope.container.subject && $scope.container.subject.modelId && (!$scope.searchType || $scope.container.subject.modelId.keyType == $scope.searchType)) {
                $scope.subjectId = $scope.container.subject.modelId.keyId;
                console.log("submit subjectId", $scope.subjectId);
                $scope.subjectType = $scope.container.subject.modelId.keyType;
                console.log("submit subjectType", $scope.subjectType);
                $scope.subject = $scope.container.subject;
            }
        };

        $scope.clearSubject = function () {
            console.log("clear subject : ",$scope.container.subject);
            //console.log("call clear subject");
            $scope.userResult = [];
            $scope.resultsArr = [];
            $scope.currentPage = 1;
            $scope.numPerPage = 10;
            //$scope.selectedSubject = {};
            $scope.container.subject = {};
            $scope.search = {queryUserTerm: '', filter:''};
            if ($scope.searchUserForm) {
                $scope.searchUserForm.$setPristine();
                $scope.searchUserForm.$setUntouched();
            }
        };

    });
