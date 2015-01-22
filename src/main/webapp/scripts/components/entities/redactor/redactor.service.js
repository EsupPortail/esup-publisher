'use strict';

angular.module('publisherApp')
    .factory('Redactor', function ($resource) {
        return $resource('api/redactors/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
