'use strict';

angular.module('publisherApp')
    .factory('Media', function ($resource, DateUtils) {
        return $resource('api/medias/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.endDate = DateUtils.convertLocalDateFromServer(data.endDate);
                        data.startDate = DateUtils.convertLocalDateFromServer(data.startDate);
                        data.validatedDate = DateUtils.convertDateTimeFromServer(data.validatedDate);
                        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate);
                        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate);
                    }
                    return data;
                }
            },
            'update': {
                method:'PUT',
                transformRequest: function(data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method:'POST',
                transformRequest: function(data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    return angular.toJson(copy);
                }
            }
        });
    });
