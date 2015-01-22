'use strict';

angular.module('publisherApp')
    .factory('UserGroupEvaluator', function ($resource) {
        return $resource('api/userGroupEvaluators/:id', {}, {
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
