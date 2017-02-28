'use strict';

angular.module('publisherApp')
    .factory('ContentDTO', function ($resource, DateUtils) {
        return $resource('api/contents/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.item.endDate = DateUtils.convertLocalDateFromServer(data.item.endDate);
                        data.item.startDate = DateUtils.convertLocalDateFromServer(data.item.startDate);
                        data.item.validatedDate = DateUtils.convertDateTimeFromServer(data.item.validatedDate);
                        data.item.createdDate = DateUtils.convertDateTimeFromServer(data.item.createdDate);
                        data.item.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.item.lastModifiedDate);
                    }
                    return data;
                }
            },
            'update': {
                method:'PUT',
                transformRequest: function(data) {
                    var copy = angular.copy(data);
                    copy.item.startDate = DateUtils.convertLocalDateToServer(copy.item.startDate);
                    copy.item.endDate = DateUtils.convertLocalDateToServer(copy.item.endDate);
                    return angular.toJson(copy);
                },
                transformResponse: function(data, headersGetter) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'save': {
                method:'POST',
                transformRequest: function(data) {
                    var copy = angular.copy(data);
                    copy.item.startDate = DateUtils.convertLocalDateToServer(copy.item.startDate);
                    copy.item.endDate = DateUtils.convertLocalDateToServer(copy.item.endDate);
                    return angular.toJson(copy);
                },
                transformResponse: function(data, headersGetter) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            }
        });
    });
