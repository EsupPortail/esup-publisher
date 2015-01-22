'use strict';

angular.module('publisherApp')
    .factory('AuthenticatedUserEvaluator', function ($resource) {
        return $resource('api/authenticatedUserEvaluators/:id', {}, {
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
