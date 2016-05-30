'use strict';

angular.module('publisherApp')
    .factory('ContentDTO', function ($resource) {
        return $resource('api/contents/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.item.createdDate = new Date(data.item.createdDate);
                    data.item.lastModifiedDate = new Date(data.item.lastModifiedDate);
                    data.item.startDate = new Date(data.item.startDate);
                    data.item.endDate = new Date(data.item.endDate);
                    return data;
                }
            },
            'update': {
                method:'PUT',
                transformRequest: function(data) {
                    var newDate = new Date();
                    newDate.setUTCDate(data.item.startDate.getDate());
                    newDate.setUTCMonth(data.item.startDate.getMonth());
                    newDate.setUTCFullYear(data.item.startDate.getFullYear());
                    data.item.startDate = newDate;
                    newDate = new Date();
                    newDate.setUTCDate(data.item.endDate.getDate());
                    newDate.setUTCMonth(data.item.endDate.getMonth());
                    newDate.setUTCFullYear(data.item.endDate.getFullYear());
                    data.item.endDate = new Date(data.item.endDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method:'POST',
                transformRequest: function(data) {
                    var newDate = new Date();
                    newDate.setUTCDate(data.item.startDate.getDate());
                    newDate.setUTCMonth(data.item.startDate.getMonth());
                    newDate.setUTCFullYear(data.item.startDate.getFullYear());
                    data.item.startDate = newDate;
                    newDate = new Date();
                    newDate.setUTCDate(data.item.endDate.getDate());
                    newDate.setUTCMonth(data.item.endDate.getMonth());
                    newDate.setUTCFullYear(data.item.endDate.getFullYear());
                    data.item.endDate = new Date(data.item.endDate);
                    return angular.toJson(data);
                }
            }
        });
    });
