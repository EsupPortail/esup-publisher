'use strict';

angular.module('publisherApp')
    .directive('subjectSearchButton', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                searchType: '@?',
                contextKey: '=',
                // subject or subjects are mandatory but only once at time, it's checked in the controller
                subject: '=?',
                subjects: '=?',
                subjectId :'=?',
                text: "@?",
                searchId: "@",
                subContextKeys: '=?',
                withExtended: "@?"
            },
            controller: 'subjectSearchButtonController',
            template: '<div class="subject-search-button">' +
            '<div ng-include="\'scripts/components/form/subjectSearch.html\'"></div></div>',
            link: function (scope, element, attrs) {
                if (angular.isUndefined(scope.subject)) {
                    scope.subject = {};
                }

                scope.$watch('subject', function(newVal, oldVal){
                    //console.log("link dir subject", scope.subject);
                    if (angular.isDefined(newVal) && !angular.equals(newVal, oldVal) && (newVal.hasOwnProperty("modelId") || newVal.hasOwnProperty("keyValue")
                    && newVal.hasOwnProperty("keyAttribute"))) {
                        $('body').removeClass('modal-open');
                        $('.modal-backdrop').remove();
                    }
                });
            }
        }
    })
    .controller('subjectSearchButtonController', function ($scope, $filter, User, Subject, Group ) {
        //console.log("init SubjectControler : ", $scope.searchType, $scope.subject, $scope.contextKey, $scope.subjectId, $scope.text, $scope.searchId);
        // searchId id usefull for modal if the tag is used more than once
        if (!angular.isDefined($scope.subject) && (!angular.isDefined($scope.subjects) || !angular.isArray($scope.subjects))) {
            throw new Error('Nor subject or subjects aren\'t defined');
        }

        $scope.currentCtxKey = angular.copy($scope.contextKey);
        $scope.subCtxs = [];
        if ($scope.subContextKeys && $scope.subContextKeys.length > 0) {
            angular.forEach($scope.subContextKeys, function(obj) {
                $scope.subCtxs.push(angular.copy(obj));
            })
        }

        $scope.userAttrs = Subject.getUserDisplayedAttrs();
        $scope.extendedAttrs = Subject.getUserFonctionalAttrs();
        $scope.userResult = [];
        $scope.resultsArr = [];

        $scope.nbtotalItems = 0;
        $scope.currentPage = 1;
        $scope.numPerPage = 10;
        $scope.listNumPerPage = [5,10,25,50,100];
        $scope.maxSize = 5;

        $scope.container = {subjects: []};

        $scope.search = {queryUserTerm: '', filter: ''};

        $scope.multiSelection = !angular.isDefined($scope.subject) || angular.isDefined($scope.subjects);

        $scope.treeCoreCtxGroups = {
            multiple: $scope.multiSelection,  // disable multiple node selection
            check_callback:true,
            ajax_params: {context : $scope.currentCtxKey, subContexts : $scope.subCtxs},
            ajax_method: 'POST'
        };
        $scope.treeCoreCtxUsersFG = {
            multiple: $scope.multiSelection,  // disable multiple node selection
            check_callback:true,
            ajax_params: {context : $scope.currentCtxKey, subContexts : $scope.subCtxs},
            ajax_method: 'POST'
        };
        $scope.treeCtxCheckboxConf= {
            three_state: false
        };

        $scope.searchUser = function() {
            //console.log("searchUser",$scope.search);
            $scope.resultsArr = [];
            $scope.userResult = [];
            $scope.search.filter = '';
            $scope.currentPage = 1;
            $scope.nbtotalItems = 0;
            /*User.search({ctx_id: $scope.contextKey.keyId, ctx_type: $scope.contextKey.keyType, criteria: $scope.search.queryUserTerm}, function(result) {
             if (result.length > 0) {
             //console.log("Array has length : ", result.length);
             $scope.userResult = $filter('orderBy')(result, 'displayName');
             $scope.nbtotalItems = $scope.userResult.length;
             //console.log("result : ", $scope.userResult);
             $scope.resultsArr = $scope.userResult.slice(0, $scope.numPerPage);
             //console.log("results paginated : ", $scope.resultsArr);
             }
             });*/
            User.search({context: $scope.currentCtxKey, subContexts: $scope.subCtxs ,search: $scope.search.queryUserTerm}, function(result) {
                if (result.length > 0) {
                    //console.log("Array has length : ", result.length);
                    $scope.userResult = $filter('orderBy')(result, 'displayName');
                    $scope.nbtotalItems = $scope.userResult.length;
                    //console.log("result : ", $scope.userResult);
                    $scope.resultsArr = $scope.userResult.slice(0, $scope.numPerPage);
                    //console.log("results paginated : ", $scope.resultsArr);
                }
            });
        };

        $scope.getUserOfGroup = function(e, data) {
            //console.log("getUserOfGroup",data);
            $scope.resultsArr = [];
            $scope.userResult = [];
            $scope.search.filter = '';
            $scope.currentPage = 1;
            $scope.nbtotalItems = 0;
            Group.userMembers({id: data.node.a_attr.model.modelId.keyId}, function(result) {
                if (result.length > 0) {
                    //console.log("Array has length : ", result.length);
                    $scope.userResult = $filter('orderBy')(result, 'displayName');
                    $scope.nbtotalItems = $scope.userResult.length;
                    $scope.resultsArr = $scope.userResult.slice(0, $scope.numPerPage);
                }
            });

        };

        $scope.initExtendedSubject = function(type) {
            $scope.clearSubject();
            $scope.container.extendedSubject = {keyAttribute: null, keyValue: null, keyType: type};
        };

        //$scope.$watch('container.subject', function() {
        //    console.log("watch container.subject", $scope.container.subject);
        //});

        $scope.$watch('currentPage', function(newVal, oldVal) {
            if (angular.isDefined(newVal) && !angular.equals(newVal, oldVal)) {
                //console.log("watch detected change on currentPage", $scope.currentPage);
                var begin = (($scope.currentPage - 1) * $scope.numPerPage)
                    , end = begin + $scope.numPerPage;
                $scope.resultsArr = filterOnDisplayName($scope.userResult,$scope.search.filter).slice(begin, end);
            }
        });

        $scope.$watch('numPerPage', function(newVal, oldVal) {
            if (angular.isDefined(newVal) && !angular.equals(newVal, oldVal)) {
                //console.log("watch detected change on numPerPage", $scope.numPerPage);
                $scope.currentPage = 1;
                $scope.resultsArr = filterOnDisplayName($scope.userResult,$scope.search.filter).slice(0, $scope.numPerPage);
            }
        });

        $scope.$watch('search.filter', function(newVal, oldVal) {
            if (angular.isDefined(newVal) && !angular.equals(newVal, oldVal)) {
                //console.log("Apply filter search");
                $scope.currentPage = 1;
                var tmpArray = filterOnDisplayName($scope.userResult, newVal);
                $scope.resultsArr = tmpArray.slice(0, $scope.numPerPage);
                $scope.nbtotalItems = tmpArray.length;
            }
        });

        function filterOnDisplayName(inputArray, criteria) {
            if(!angular.isDefined(criteria) || criteria == ''){
                return inputArray;
            }
            var data=[];
            angular.forEach(inputArray, function(item){
                if(item.displayName.toLowerCase().indexOf(criteria.toLowerCase()) != -1){
                    //console.log("filter keep: ", item.displayName, criteria);
                    data.push(item);
                }
            });
            return data;
        }

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
            //console.log("Checked ", data.node.a_attr.model);
            checkSubject(data.node.a_attr.model);
            $scope.$apply();
        };

        $scope.uncheckGroup = function(e, data) {
            //console.log("unchecked ", data.node.a_attr.model);
            uncheckSubject(data.node.a_attr.model);
            $scope.$apply();
        };

        $scope.toggleUser = function(model) {
            //console.log("toggle user ", model);
             if (angular.isDefined(model.modelId) && !angular.equals({}, model.modelId)) {
                 if ($scope.isSubjectKeySelected(model.modelId)) {
                     uncheckSubject(model)
                 } else {
                     checkSubject(model);
                 }
             }
        };

        function checkSubject(model){
            if (angular.isDefined(model.modelId) && !angular.equals({}, model.modelId)
                && (!$scope.searchType || model.modelId.keyType == $scope.searchType)) {
                $scope.container.subjects.push(model);
                //console.log("selected list of subjects : ", $scope.container.subjects);
            }
        }

        function uncheckSubject(model){
            if (angular.isDefined(model.modelId) && !angular.equals({}, model.modelId)
                && (!$scope.searchType || model.modelId.keyType == $scope.searchType)) {
                $scope.container.subjects = $scope.container.subjects.filter(function (element) {
                    return !angular.equals(element.modelId, model.modelId);
                });
                //console.log("selected list of subjects : ", $scope.container.subjects);
            }
        }

        $scope.isSubjectKeySelected = function(subjectKey) {
            var list = $scope.container.subjects;
            if (list.length > 0) {
                for (var i = 0, size = list.length; i < size; i++) {
                    //console.log("checking equals ", list[i], subjectKey);
                    if (angular.equals(list[i].modelId, subjectKey)) {
                        return true;
                    }
                }
            }
            return false;
        };

        $scope.submitSubject = function() {
            //console.log("submitSubject Selected subject : ",$scope.container.subjects);
            if ($scope.container.subjects && angular.isArray($scope.container.subjects) && $scope.container.subjects.length > 0) {
                if ($scope.multiSelection) {
                    $scope.subjects = $scope.container.subjects;
                } else {
                    if (angular.isDefined($scope.subjectId)) {
                        $scope.subjectId = $scope.container.subjects[$scope.container.subjects.length - 1].modelId.keyId;
                        //console.log("submit subjectId", $scope.subjectId);
                    }
                    //$scope.subjectType = $scope.container.subject.modelId.keyType;
                    //console.log("submit subjectType", $scope.subjectType);
                    $scope.subject = $scope.container.subjects[$scope.container.subjects.length - 1];
                }
            } else if (angular.isDefined($scope.container.subject) && angular.isDefined($scope.container.subject.modelId) && !angular.equals({},$scope.container.subject.modelId)){
                if ($scope.multiSelection) {
                    $scope.subjects = [];
                    $scope.subjects.push($scope.container.subject);
                } else {
                    if (angular.isDefined($scope.subjectId)) {
                        $scope.subjectId = $scope.container.subject.modelId.keyId;
                    }
                    $scope.subject = $scope.container.subject;
                }
            } else if (angular.isDefined($scope.container.extendedSubject) && !angular.equals({},$scope.container.extendedSubject)){
                    if (angular.isDefined($scope.subjectId)) {
                        $scope.subjectId = $scope.container.extendedSubject.keyValue + $scope.container.extendedSubject.keyAttribute;
                    }
                    $scope.subject = $scope.container.extendedSubject;
            } else {
                if ($scope.multiSelection) {
                    $scope.subjects = [];
                } else {
                    if (angular.isDefined($scope.subjectId)) {
                        $scope.subjectId = {};
                    }
                    $scope.subject = {};
                }
            }
            //console.log("submit subject and subjects", $scope.subject, $scope.subjects);
        };

        $scope.canSubmit = function() {
            return (angular.isDefined($scope.container.subject) && angular.isDefined($scope.container.subject.modelId)
                    && !angular.equals({},$scope.container.subject.modelId))
                || (angular.isDefined($scope.container.extendedSubject) && !angular.equals({},$scope.container.extendedSubject))
                || (angular.isDefined($scope.container.subjects) && $scope.container.subjects.length > 0
                    && angular.isDefined($scope.container.subjects[$scope.container.subjects.length - 1].modelId)
                    && !angular.equals({},$scope.container.subjects[$scope.container.subjects.length - 1].modelId));
        };

        $scope.clearSubject = function () {
            //console.log("clear subject : ",$scope.container.subject);
            //console.log("call clear subject");
            $scope.userResult = [];
            $scope.resultsArr = [];
            $scope.currentPage = 1;
            $scope.numPerPage = 10;
            $scope.container.subjects = [];
            delete $scope.container.initExtendedSubject;
            delete $scope.subject;
            delete $scope.subjects;
            delete $scope.subjectId;
            $('#jsTreeGroup' + $scope.searchId).jstree(true).deselect_all();
            $('#jsTreeUserFromGroup' + $scope.searchId).jstree(true).deselect_all();
            $scope.search = {queryUserTerm: '', filter:''};
            if ($scope.searchUserForm) {
                $scope.searchUserForm.$setPristine();
                $scope.searchUserForm.$setUntouched();
            }
        };

    });
