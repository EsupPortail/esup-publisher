'use strict';

angular.module('publisherApp')
    .factory('UserAttributesEvaluator', function ($resource) {
        return $resource('api/userAttributesEvaluators/:id', {}, {
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
