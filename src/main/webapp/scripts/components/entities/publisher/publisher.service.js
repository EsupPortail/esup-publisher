'use strict';

angular.module('publisherApp')
    .factory('Publisher', function ($resource) {
        return $resource('api/publishers/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.createdDate = new Date(data.createdDate);
                    data.lastModifiedDate = new Date(data.lastModifiedDate);
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
