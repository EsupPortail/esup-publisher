'use strict';

angular.module('publisherApp')
    .factory('UserMultivaluedAttributesEvaluator', function ($resource) {
        return $resource('api/userMultivaluedAttributesEvaluators/:id', {}, {
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
