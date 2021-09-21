'use strict';

angular.module('publisherApp')
  .factory('AllEnums', function ($resource) {
      return $resource('api/enums/all/', {}, {
          'query': { method: 'GET', isArray: false}
      });
  }).factory('EnumDatas', function($q, $state, AllEnums) {
        var AccessTypeList,ClassificationDecorTypeList,ContextTypeList,DisplayOrderTypeList,FilterTypeList,ItemStatusList,ItemTypeList,OperatorTypeList,PermissionClassList,
            PermissionTypeList, StringEvaluationModeList, SubjectTypeList, SubscribeTypeList, WritingModeList, WritingFormatList;
        return {
            init : function () {
                return $q.all([AllEnums.query().$promise])
                    .then(function (results) {
                        if (results) {
                            results = angular.fromJson(results[0]);
                            AccessTypeList = results.AccessType;
                            ContextTypeList = results.ContextType;
                            DisplayOrderTypeList = results.DisplayOrderType;
                            FilterTypeList = results.FilterType;
                            ItemStatusList = results.ItemStatus;
                            ItemTypeList = results.ItemType;
                            OperatorTypeList = results.OperatorType;
                            PermissionClassList = results.PermissionClass;
                            PermissionTypeList = results.PermissionType;
                            StringEvaluationModeList = results.StringEvaluationMode;
                            SubjectTypeList = results.SubjectType;
                            SubscribeTypeList = results.SubscribeType;
                            WritingModeList = results.WritingMode;
                            WritingFormatList = results.WritingFormat;
                            ClassificationDecorTypeList = results.ClassificationDecorType;
                        }
                    }).catch(function (error) {
                        console.log(JSON.stringify(error));
                        $state.go("error");
                    });
            },
            getAccessTypeList : function () {
                if (!AccessTypeList) {this.init();}
                return AccessTypeList;
            },
            getClassificationDecorTypeList : function () {
                if (!ClassificationDecorTypeList) {this.init();}
                return ClassificationDecorTypeList;
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