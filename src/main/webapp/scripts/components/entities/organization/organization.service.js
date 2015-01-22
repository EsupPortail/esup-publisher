'use strict';

angular.module('publisherApp')
    .factory('Organization', function ($resource) {
        return $resource('api/organizations/:id', {}, {
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
            'update': { method:'PUT' }
        });
    });
