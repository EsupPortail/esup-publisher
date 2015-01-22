'use strict';

angular.module('publisherApp')
    .directive('evaluator', ['$compile', function($compile) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                evaluator: '='
            },
            link: function (scope, element, attrs) {
                var init = function () {
                    if (angular.isDefined(scope.evaluator) && angular.isDefined(scope.evaluator.class)) {
                        console.log("evaluator traitée :", scope.evaluator);
                        switch (scope.evaluator.class) {
                            case "OPERATOR":
                                //var html = '<operator-evaluator evaluator="evaluator" ></operator-evaluator>';
                                var html;
                                if (attrs.simple) {
                                    html = "<simple-evaluators collection='evaluator.evaluators'></simple-evaluators>";
                                    if (attrs.ischild || scope.evaluator.type != "OR") {
                                        html = '<span translate="publisherApp.evaluators.forAdvancedOnly"></span>'
                                    }
                                } else {
                                    html = "<span>{{evaluator.type}}</span><evaluators collection='evaluator.evaluators'></evaluators>";
                                }
                                if (attrs.ischild || attrs.simple) {
                                    element.append(html);
                                } else {
                                    element.append('<ul class="evaluator"><li>' + html + '</li></ul>');
                                }
                                break;
                            case "AUTHENTICATED":
                                if (!attrs.simple ) {
                                    element.append('<span translate="publisherApp.evaluators.AuthenticatedUsers.text"></span>');
                                } else {
                                    element.append('<span translate="publisherApp.evaluators.forAdvancedOnly"></span>');
                                }
                                break;
                            case "USERATTRIBUTES":
                            case "USERMULTIVALUEDATTRIBUTES":
                                //element.append('<user-attributes-evaluator evaluator="evaluator" ></user-attributes-evaluator>');
                                if (scope.evaluator.attribute == "uid" && scope.evaluator.mode == "EQUALS") {
                                    scope.userModelId = {keyType: 'PERSON', keyId: scope.evaluator.value};
                                    if (!attrs.simple) {
                                        element.append('<subject-infos subject="userModelId" >' +
                                            '<span translate="publisherApp.evaluators.UserAttribute.subjetIs"></span>&nbsp;</subject-infos>');
                                    } else {
                                        element.append('<subject-infos subject="userModelId"></subject-infos>');
                                    }
                                } else {
                                    if (!attrs.simple) {
                                        element.append('<span translate="publisherApp.evaluators.UserAttribute.attribute" translate-values="{attribute: \'{{evaluator.attribute}}\'}"></span>'
                                            + '<span translate="publisherApp.evaluators.UserAttribute.mode" translate-values="{mode: \'{{evaluator.mode}}\'}"></span>'
                                            + '<span translate="publisherApp.evaluators.UserAttribute.value" translate-values="{value: \'{{evaluator.value}}\'}"></span>');
                                    } else {
                                        element.append('<span translate="publisherApp.evaluators.forAdvancedOnly"></span>');
                                    }
                                }
                                break;
                            case "USERGROUP":
                                //element.append('<group-evaluator evaluator="evaluator" ></group-evaluator>');
                                scope.groupModelId = {keyType: 'GROUP', keyId: scope.evaluator.group};
                                if (!attrs.simple) {
                                    element.append('<subject-infos subject="groupModelId">' +
                                        '<span translate="publisherApp.evaluators.UserGroup.memberOf"></span>&nbsp;</subject-infos>');
                                } else {
                                    element.append('<subject-infos subject="groupModelId"></subject-infos>');
                                }
                                break;
                            default :
                                if (!attrs.simple ) {
                                    element.append('<span translate="publisherApp.evaluators.Unknown"></span>');
                                } else {
                                    element.append('<span translate="publisherApp.evaluators.forAdvancedOnly"></span>');
                                }
                                break;
                        }
                        $compile(element.contents())(scope)
                    }
                };

                scope.$watch('evaluator', function() {
                    if (angular.isDefined(scope.evaluator)) {
                        init();
                    } else {
                        console.log("evaluator is undefined !");
                    }
                });
            }
        }
    }])
    .directive('evaluators', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                collection: '='
            },
            template: "<ul class='evaluator'><li ng-repeat='member in collection' ><evaluator evaluator='member' ischild='true'></evaluator></li></ul>"
        }
    })
    .directive('simpleEvaluators', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                collection: '='
            },
            template: "<ul class='evaluator'><li ng-repeat='member in collection' ><evaluator evaluator='member' ischild='true' simple='true'></evaluator></li></ul>"
        }
    })
    .directive('editEvaluator', ['$compile', '$filter', function($compile, $filter) {
        function isValidEvaluator(evaluator) {
            //console.log("isValidEvaluator", evaluator);
            if (angular.isDefined(evaluator) && angular.isDefined(evaluator.class)) {
                switch (evaluator.class) {
                    case "OPERATOR":
                        if (evaluator.evaluators.length > 0) {
                            for (var i = 0; i < evaluator.evaluators.length; i++) {
                                if (!isValidEvaluator(evaluator.evaluators[i])) {
                                    return false;
                                }
                            }
                            return true;
                        }
                        return false;
                    case "AUTHENTICATED": return true;
                    case "USERATTRIBUTES":
                    case "USERMULTIVALUEDATTRIBUTES":
                        return (angular.isDefined(evaluator.value) && angular.isDefined(evaluator.attribute) && angular.isDefined(evaluator.mode) &&
                        !angular.equals("", evaluator.value) && !angular.equals("",evaluator.attribute) && !angular.equals("",evaluator.mode) &&
                        !angular.equals(null, evaluator.value) && !angular.equals(null,evaluator.attribute) && !angular.equals(null,evaluator.mode));
                    case "USERGROUP":
                        return angular.isDefined(evaluator.group);
                    default :
                        return false;
                }
            }
            return false;
        }

        return {
            restrict: "E",
            replace: true,
            scope: {
                evaluator: '=',
                operators: '=',
                userAttributes: '=',
                stringEvaluators: "=",
                isValid: "=",
                contextKey: "="
            },
            link: { pre :function (scope, element, attrs) {
                var buttons = '<div class="input-group" role="group"><div class="input-group-btn" role="group">' +
                    '<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
                    '{{ \'publisherApp.evaluators.add.button\' | translate }} <span class="caret"></span></button>' +
                    '<ul class="dropdown-menu">' +
                    '<li><a href="#" ng-click="addEvaluator(evaluator,\'OPERATOR\')">{{ \'publisherApp.evaluators.add.Operator\' | translate }}</a></li>' +
                    '<li><a href="#" ng-click="addEvaluator(evaluator,\'USER\')">{{ \'publisherApp.evaluators.add.User\' | translate }}</a></li>' +
                    '<li><a href="#" ng-click="addEvaluator(evaluator,\'GROUP\')">{{ \'publisherApp.evaluators.add.Group\' | translate }}</a></li>' +
                    '<li><a href="#" ng-click="addEvaluator(evaluator,\'AUTH\')">{{ \'publisherApp.evaluators.add.AuthenticatedUsers\' | translate }}</a></li>' +
                    '<li><a href="#" ng-click="addEvaluator(evaluator,\'ATTRIBUTE\')">{{ \'publisherApp.evaluators.add.UserAttribute\' | translate }}</a></li>' +
                    '</ul></div>' +
                    '<button type="button" class="btn btn-default" ng-click="deleteEvaluators(evaluator)" ng-show="evaluator.evaluators && evaluator.evaluators.length > 0">' +
                    '<span>{{ \'publisherApp.evaluators.Operator.removeChilds\' | translate }}</span></button></div>';

                var delElm = '&nbsp;<a href ng-click="deleteEvaluator(evaluator, $parent.$parent.$parent.evaluator.evaluators)" ' +
                    'uib-tooltip="{{ \'publisherApp.evaluators.delete.button\' | translate}}">' +
                    '<i class="glyphicon glyphicon-remove-circle text-danger" ></i></a>';

                var generateId = function() {
                    return Date.now();
                };

                var init = function () {
                    //console.log("models ",scope.evaluator, scope.operators, scope.stringEvaluators, scope.userAttributes);
                    if (angular.isUndefined(attrs.operators) || angular.isUndefined(attrs.evaluator) || angular.isUndefined(attrs.stringEvaluators)
                        || angular.isUndefined(attrs.userAttributes) || angular.isUndefined(attrs.contextKey)) {
                        return;
                    }
                    if (angular.isDefined(scope.evaluator) && angular.isDefined(scope.evaluator.class)) {
                        switch (scope.evaluator.class) {
                            case "OPERATOR":
                                var html;
                                if (attrs.hasOwnProperty("ischild")) {
                                    html = '<div class="form-group form-inline"><select class="form-control" ng-model="evaluator.type" ' +
                                        'ng-options="operator for operator in operators"></select>' + buttons + delElm +
                                        '</div></div><edit-evaluators collection="evaluator.evaluators" operators="operators" string-evaluators="stringEvaluators" ' +
                                        'user-attributes="userAttributes" context-key="contextKey"></edit-evaluators>';
                                    element.append(html);
                                } else {
                                    html = '<div class="form-group form-inline"><select class="form-control" ng-model="evaluator.type" ' +
                                        'ng-options="operator for operator in operators"></select>' + buttons +
                                        '</div></div><edit-evaluators collection="evaluator.evaluators" operators="operators" string-evaluators="stringEvaluators" ' +
                                        'user-attributes="userAttributes" context-key="contextKey"></edit-evaluators>';
                                    element.append('<ul class="evaluator"><li>' + html + '</li></ul>');
                                }
                                break;
                            case "AUTHENTICATED":
                                element.append('<div class="form-group form-inline"><span translate="publisherApp.evaluators.AuthenticatedUsers.text"></span>' + delElm + '</div>');
                                break;
                            case "USERATTRIBUTES":
                            case "USERMULTIVALUEDATTRIBUTES":
                                if ((scope.evaluator.value && scope.evaluator.attribute == "uid" && scope.evaluator.mode == "EQUALS")) {
                                    scope.userModelId = {keyType: 'PERSON', keyId: scope.evaluator.value};
                                    element.append('<div class="form-group"><subject-infos subject="userModelId">'
                                        + '<span translate="publisherApp.evaluators.UserAttribute.subjetIs"></span>&nbsp;</subject-infos>' + delElm + '</div>');
                                } else if (scope.evaluator.attribute == "uid" && scope.evaluator.mode == "EQUALS") {
                                    element.append('<div class="form-group form-inline"><subject-search-button subject="subjectNotUsed" search-type="PERSON" ' +
                                        'context-key="contextKey" class="input-group" search-id="' + generateId() + '" subject-id="evaluator.value"></subject-search-button>' + delElm + '</div>');
                                } else {
                                    element.append('<div class="form-group form-inline">' +
                                        '<select class="form-control" ng-model="evaluator.attribute" ng-options="attribute for attribute in userAttributes"></select>' +
                                        '<select class="form-control" ng-model="evaluator.mode" ng-options="mode for mode in stringEvaluators"></select>' +
                                        '<input type="text" class="form-control" ng-model="evaluator.value" ng-model-options="{debounce: 1500}" required>' + delElm + '</div>');
                                }
                                //element.append('<user-attributes-evaluator evaluator="evaluator" ></user-attributes-evaluator>');
                                break;
                            case "USERGROUP":
                                if (scope.evaluator.group && !angular.equals(scope.evaluator.group,"")) {
                                    scope.userModelId = {keyType: 'GROUP', keyId: scope.evaluator.group};
                                    element.append('<div class="form-group">' +
                                        '<subject-infos subject="userModelId"><span translate="publisherApp.evaluators.UserGroup.memberOf"></span>&nbsp;</subject-infos>' + delElm + '</div>');
                                    //element.append('<div class="form-group form-inline"><span>{{ \'publisherApp.evaluators.UserGroup.memberOf\' | translate }}
                                    // </span><span> {{evaluator.group}}</span>' + delElm + '</div>');
                                } else {
                                    element.append('<div class="form-group form-inline"><subject-search-button subject="subjectNotUsed" search-type="GROUP" ' +
                                        'context-key="contextKey" class="input-group" search-id="' + generateId() + '" subject-id="evaluator.group"></subject-search-button>' + delElm + '</div>');
                                }
                                break;
                            default :
                                element.append('<div class="form-group form-inline"><span translate="publisherApp.evaluators.Unknown.text"></span>' + delElm + '</div>');
                                break;
                        }
                        $compile(element.contents())(scope)
                    }
                };

                scope.$watch('evaluator', function(newv, oldv) {
                    element.children().remove();
                    init();
                    if (!attrs.hasOwnProperty("ischild")) {
                        scope.isValid = isValidEvaluator(scope.evaluator);
                       // console.log("isValid =", scope.isValid);
                    }
                }, true);

                scope.$watch('evaluator.type', function(newv, oldv) {
                    if (newv == "NOT" && scope.evaluator.evaluators.length > 1) {
                        var unique = scope.evaluator.evaluators[0];
                        scope.evaluator.evaluators = [];
                        scope.evaluator.evaluators.push(unique);
                    }
                });
            }},
            controller: function($scope, $element) {
                $scope.deleteEvaluators = function(data) {
                    data.evaluators = [];
                };
                $scope.deleteEvaluator = function(data, array) {
                    console.log("deleteEvaluator ", data, array);
                    var i = 0, idx=-1;
                    for (var size = array.length; i < size; i++) {
                        //console.log("checking equals ", $scope.$parent.classifications[i], contextKey);
                        if (angular.equals(array[i], data)) {
                            idx = i;
                            break;
                        }
                    }
                    // is currently selected
                    if (idx > -1) {
                        array.splice(idx, 1);
                    }
                };
                $scope.addEvaluator = function(data, type) {
                    //console.log("Add evalutor type", type, data);
                    //var post = data.evaluators.length + 1;
                    //var newName = data.name + '-' + post;
                    switch (type) {
                        case 'OPERATOR':
                            data.evaluators.push({class: 'OPERATOR', id: null, type: 'OR', evaluators: []});
                            break;
                        case 'USER':
                            data.evaluators.push({class: 'USERATTRIBUTES',id: null, mode: "EQUALS", attribute: "uid", value: null});
                            break;
                        case 'GROUP':
                            data.evaluators.push({class: 'USERGROUP',id: null, group: null});
                            break;
                        case 'AUTH':
                            data.evaluators.push({class: 'AUTHENTICATED', id: null});
                            break;
                        case 'ATTRIBUTE':
                            data.evaluators.push({class: 'USERMULTIVALUEDATTRIBUTES',id: null, mode: $scope.stringEvaluators[0], attribute: $scope.userAttributes[0], value: null});
                            break;
                    }
                };
                $scope.userAttributes = $filter('filter')($scope.userAttributes, "!uid", true);
            }
        }
    }]).directive('editEvaluators', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                collection: '=',
                operators: '=',
                userAttributes: '=',
                stringEvaluators: "=",
                contextKey: "="
            },
            template: "<ul class='edit-evaluator'><li ng-repeat='member in collection' >" +
            "<edit-evaluator evaluator='member' ischild operators='operators' string-evaluators='stringEvaluators' user-attributes='userAttributes' context-key='contextKey'>" +
            "</edit-evaluator></li></ul>"
        }
    });
