'use strict';

angular.module('publisherApp')
    .factory('Media', function ($resource) {
        return $resource('api/medias/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    var endDateFrom = data.endDate.split("-");
                    data.endDate = new Date(new Date(endDateFrom[0], endDateFrom[1] - 1, endDateFrom[2]));
                    var startDateFrom = data.startDate.split("-");
                    data.startDate = new Date(new Date(startDateFrom[0], startDateFrom[1] - 1, startDateFrom[2]));
                    if (data.validatedDate)
                        data.validatedDate = new Date(data.validatedDate);
                    data.createdDate = new Date(data.createdDate);
                    data.lastModifiedDate = new Date(data.lastModifiedDate);
                    return data;
                }
            },
            'update': {
                method:'PUT',
                transformRequest: function(data) {
                    var newDate = new Date();
                    newDate.setUTCDate(data.startDate.getDate());
                    newDate.setUTCMonth(data.startDate.getMonth());
                    newDate.setUTCFullYear(data.startDate.getFullYear());
                    data.startDate = newDate;
                    newDate = new Date();
                    newDate.setUTCDate(data.endDate.getDate());
                    newDate.setUTCMonth(data.endDate.getMonth());
                    newDate.setUTCFullYear(data.endDate.getFullYear());
                    data.endDate = new Date(data.endDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method:'POST',
                transformRequest: function(data) {
                    var newDate = new Date();
                    newDate.setUTCDate(data.startDate.getDate());
                    newDate.setUTCMonth(data.startDate.getMonth());
                    newDate.setUTCFullYear(data.startDate.getFullYear());
                    data.startDate = newDate;
                    newDate = new Date();
                    newDate.setUTCDate(data.endDate.getDate());
                    newDate.setUTCMonth(data.endDate.getMonth());
                    newDate.setUTCFullYear(data.endDate.getFullYear());
                    data.endDate = new Date(data.endDate);
                    return angular.toJson(data);
                }
            }
        });
    });
