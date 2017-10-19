'use strict';
// if not explicitly set resolveKey to false it will try to resolve the subject
angular.module('publisherApp')
    .directive('subjectInfos', ['$compile', 'Subject', '$q', '$translate', function($compile, Subject, $q, $translate) {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                subject: '=',
                resolveKey: '@?',
                userDisplayedAttrs: '=?'
            },
            controller: function($scope) {
                $scope.userAttrs = $scope.userDisplayedAttrs || Subject.getUserDisplayedAttrs();

                if (!angular.isDefined($scope.notFoundSubjectMsg)) {
                    $translate('publisherApp.subject.disappear').then(function (translatedValue) {
                        $scope.notFoundSubjectMsg = translatedValue;
                    });
                }

                // to resolve and complete subject
                if (angular.isDefined($scope.subject) && angular.isDefined($scope.subject.modelId)
                    && !angular.isDefined($scope.subject.attributes) && !angular.equals('GROUP',$scope.subject.modelId.keyType)) {
                    // is DTO
                    if (!angular.isDefined($scope.resolveKey) || !angular.equals('false',$scope.resolveKey)) {
                        Subject.getSubjectInfos($scope.subject.modelId.keyType, $scope.subject.modelId.keyId).$promise.then(function(result) {
                            $scope.subject = result;
                        });
                    }
                } else if (angular.isDefined($scope.subject) && $scope.subject.hasOwnProperty('keyType') && $scope.subject.hasOwnProperty('keyId')){
                    // is key Id
                    if (!angular.isDefined($scope.resolveKey) || !angular.equals('false',$scope.resolveKey)) {
                        Subject.getSubjectInfos($scope.subject.keyType, $scope.subject.keyId).$promise.then(function(result) {
                            $scope.subject = result;
                        });
                    }
                }

                $scope.tooltipSubject = function(subject) {
                    //console.log("tooltipSubject :", subject, $scope.userAttrs);
                    if (!angular.isDefined($scope.userAttrs) || !angular.isDefined(subject)) return '';
                    if (angular.isDefined(subject.keyId)) {
                        return "'" + subject.keyId + "'" + $scope.notFoundSubjectMsg;
                    }
                    if (!angular.isDefined(subject.modelId)) return '';
                    if (angular.isDefined(subject.modelId) && subject.foundOnExternalSource !== true) {
                        return "'" + subject.modelId.keyId + "'" + $scope.notFoundSubjectMsg;
                    }
                    if (subject.modelId.keyType =='GROUP') {
                        return subject.modelId.keyId;
                    }
                    if (!angular.isDefined(subject.attributes)) return subject.modelId.keyId;
                    var index;
                    var attrs = subject.attributes;
                    var html = '';
                    for (index = 0; index < $scope.userAttrs.length; ++index){
                        if (index > 0 && angular.isDefined(attrs[$scope.userAttrs[index]]) && !angular.equals('', html)) {
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
                    //console.log("html :", html);
                    return html;
                };
            },
            template: '<div ng-transclude class="subject-infos"></div>',
            link: function (scope, element, attrs) {

                var init = function() {
                    element.show();
                    var subject = scope.subject;
                    element.children('.subject-infos').remove();
                    //console.log("subjectInfos :", subject, element);
                    var html;
                    if (subject && !angular.equals({}, subject)) {
                        if (subject.hasOwnProperty('modelId')) {
                            html = htmlFromDTO(subject);
                        } else if (subject.hasOwnProperty('keyType') && subject.hasOwnProperty('keyId')) {
                            html = htmlFromKey(subject);
                        } else if (subject.hasOwnProperty('keyValue') && subject.hasOwnProperty('keyAttribute') && subject.hasOwnProperty('keyType')) {
                            html = htmlFromExtendedKey(subject);
                        }
                    }
                    append(html);
                    $compile(element.contents())(scope)
                };

                scope.$watch('subject', function (newSubj, oldSubj) {
                    /* hack to avoid angular problem of scope object not updated in parent scope due to ng-repeat*/
                    if (!angular.isDefined(newSubj.modelId) && angular.isDefined(oldSubj.modelId)) {
                        scope.subject = oldSubj;
                    }
                    init();
                });

                function append (html){
                    if (html) {
                        element.append(html);
                    } else {
                        element.hide();
                    }
                }

                function htmlFromDTO (subject) {
                    var displayName = '';
                    var css;
                    var elem = '<span class="fa fa-question subject-infos"></span>';
                    var type = subject.modelId.keyType;
                    var id = subject.modelId.keyId;
                    var cssnotfound = '';
                    switch (type) {
                        case "GROUP" :
                            css = "fa fa-users subject-infos";
                            break;
                        case "PERSON":
                            css = "fa fa-user subject-infos";
                            break;
                    }
                    if (subject.foundOnExternalSource === true || (subject.displayName && subject.displayName.length > 0)) {
                        displayName = subject.displayName;
                    } else {//if (type && id) {
                        //displayName = "[" + type + ", " + id + "]" + scope.notFoundSubjectMsg;
                        cssnotfound = "fa fa-question";
                    }
                    if (css) {
                        elem = '<a href class="' + css + '" data-subject-id="' + id + '"><span class="' + cssnotfound + '" uib-tooltip-placement="top" uib-tooltip="{{ tooltipSubject(subject) }}"> ' + displayName + '</span></a>';
                    }
                    //console.log("htmlFromDTO returned : ", elem);
                    return elem;
                }

                function htmlFromKey (subject) {
                    var css;
                    var elem = '<span class="fa fa-question subject-infos"></span>';
                    //console.log("subjectInfos htmlFromKey", subject);
                    var type = subject.keyType;
                    var id = subject.keyId;
                    switch (type) {
                        case "GROUP" :
                            css = "fa fa-users subject-infos";
                            break;
                        case "PERSON":
                            css = "fa fa-user subject-infos";
                            break;
                        default: throw "Subject Type not managed and should not be tested :" + type;
                            break;
                    }
                    if (css) {
                        elem = '<a href class="' + css + '" data-subject-id="' + id + '"><span class="fa fa-question" uib-tooltip-placement="top" uib-tooltip="{{ tooltipSubject(subject) }}"></span></a>';
                    }
                    //console.log("htmlFromKey returned : ", elem);
                    return elem;
                }

                function htmlFromExtendedKey (subject) {
                    var css = "fa fa-users subject-infos";
                    var elem = '<span class="fa fa-question subject-infos"></span>';
                    console.log("subjectInfos htmlFromExtendedKey", subject);
                    var type = subject.keyType;
                    var id = subject.keyValue;
                    var attr = subject.keyAttribute;
                    switch (type) {
                        case "GROUP" :
                        case "PERSON":
                            css = "fa fa-user subject-infos";
                            elem = '<a href class="' + css + '" data-subject-id="' + id + '">' +
                                '<span class="fa fa-question" uib-tooltip-placement="top" uib-tooltip="{{ tooltipSubject(subject) }}"></span></a>';
                            break;
                        case "PERSON_ATTR" :
                        case "PERSON_ATTR_REGEX" :
                            elem = '<span class="' + css + '" data-subject-id="' + id + '"> ' + attr + " = \"" + id + '\"</span>';
                            break;
                        default : throw "Subject Type not managed :" + type;
                    }
                    //console.log("htmlFromExtendedKey returned : ", elem);
                    return elem;
                }
            }
        };
    }]);

