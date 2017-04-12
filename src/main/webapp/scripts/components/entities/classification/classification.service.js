'use strict';

angular.module('publisherApp')
    .factory('Classification', function ($resource, DateUtils) {
        return $resource('api/classifications/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate);
                        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'hightlihted': {
                method: 'GET',
                url: 'api/classifications/highlighted'
            }
        });
    });
