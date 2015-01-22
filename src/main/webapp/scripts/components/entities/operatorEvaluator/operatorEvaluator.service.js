'use strict';

angular.module('publisherApp')
    .factory('OperatorEvaluator', function ($resource) {
        return $resource('api/operatorEvaluators/:id', {}, {
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
