'use strict';

angular.module('publisherApp')
    .factory('Publisher', function ($resource, DateUtils) {
        return $resource('api/publishers/:id', {}, {
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
            'modify': {
                method: 'PUT', isArray: true,
                params: { action:'modify' }
            }

        });
    });
