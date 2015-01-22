'use strict';

angular.module('publisherApp')
    .factory('AccessType', function ($resource) {
        return $resource('api/enums/accesstype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('ContextType', function ($resource) {
        return $resource('api/enums/contexttype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('DisplayOrderType', function ($resource) {
        return $resource('api/enums/displayordertype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('FilterType', function ($resource) {
        return $resource('api/enums/filtertype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('ItemStatus', function ($resource) {
        return $resource('api/enums/itemstatus/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('ItemType', function ($resource) {
        return $resource('api/enums/itemtype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('OperatorType', function ($resource) {
        return $resource('api/enums/operatortype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('PermissionClass', function ($resource) {
        return $resource('api/enums/permissionclass/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('PermissionType', function ($resource) {
        return $resource('api/enums/permissiontype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('StringEvaluationMode', function ($resource) {
        return $resource('api/enums/stringevaluationmode/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('SubjectType', function ($resource) {
        return $resource('api/enums/subjecttype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('SubscribeType', function ($resource) {
        return $resource('api/enums/subscribetype/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('WritingMode', function ($resource) {
        return $resource('api/enums/writingmode/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('WritingFormat', function ($resource) {
        return $resource('api/enums/writingformat/', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }).factory('EnumDatas', function($q, $state, AccessType, ContextType, DisplayOrderType, FilterType,
                                     ItemStatus, ItemType, OperatorType, PermissionClass, PermissionType, StringEvaluationMode, SubjectType,
                                     SubscribeType, WritingMode, WritingFormat) {
        var AccessTypeList,ContextTypeList,DisplayOrderTypeList,FilterTypeList,ItemStatusList,ItemTypeList,OperatorTypeList,PermissionClassList,
            PermissionTypeList, StringEvaluationModeList, SubjectTypeList, SubscribeTypeList, WritingModeList, WritingFormatList;
        return {
            init : function () {
                return $q.all([AccessType.query().$promise, ContextType.query().$promise, DisplayOrderType.query().$promise,
                    FilterType.query().$promise, ItemStatus.query().$promise, ItemType.query().$promise, OperatorType.query().$promise,
                    PermissionClass.query().$promise, PermissionType.query().$promise, StringEvaluationMode.query().$promise,
                    SubjectType.query().$promise, SubscribeType.query().$promise, WritingMode.query().$promise, WritingFormat.query().$promise])
                    .then(function (results) {
                        AccessTypeList = results[0];
                        ContextTypeList = results[1];
                        DisplayOrderTypeList = results[2];
                        FilterTypeList = results[3];
                        ItemStatusList = results[4];
                        ItemTypeList = results[5];
                        OperatorTypeList = results[6];
                        PermissionClassList = results[7];
                        PermissionTypeList = results[8];
                        StringEvaluationModeList = results[9];
                        SubjectTypeList = results[10];
                        SubscribeTypeList = results[11];
                        WritingModeList = results[12];
                        WritingFormatList = results[13];
                    }).catch(function (error) {
                        console.log(JSON.stringify(error));
                        $state.go("error");
                    });
            },
            getAccessTypeList : function () {
                if (!AccessTypeList) {this.init();}
                return AccessTypeList;
            },
            getContextTypeList : function () {
                if (!ContextTypeList) {this.init();}
                return ContextTypeList;
            },
            getDisplayOrderTypeList : function () {
                if (!DisplayOrderTypeList) {this.init();}
                return DisplayOrderTypeList;
            },
            getFilterTypeList : function () {
                if (!FilterTypeList) {this.init();}
                return FilterTypeList;
            },
            getItemStatusList : function () {
                if (!ItemStatusList) {this.init();}
                return ItemStatusList;
            },
            getItemTypeList : function () {
                if (!ItemTypeList) {this.init();}
                return ItemTypeList;
            },
            getOperatorTypeList : function () {
                if (!OperatorTypeList) {this.init();}
                return OperatorTypeList;
            },
            getPermissionClassList : function () {
                if (!PermissionClassList) {this.init();}
                return PermissionClassList;
            },
            getPermissionTypeList : function () {
                if (!PermissionTypeList) {this.init();}
                return PermissionTypeList;
            },
            getStringEvaluationModeList : function () {
                if (!StringEvaluationModeList) {this.init();}
                return StringEvaluationModeList;
            },
            getSubjectTypeList : function () {
                if (!SubjectTypeList) {this.init();}
                return SubjectTypeList;
            },
            getSubscribeTypeList : function () {
                if (!SubscribeTypeList) {this.init();}
                return SubscribeTypeList;
            },
            getWritingModeList : function() {
                if (!WritingModeList) {this.init();}
                return WritingModeList;
            },
            getWritingFormatList : function() {
                if (!WritingFormatList) {this.init();}
                return WritingFormatList;
            }
        }
    });
