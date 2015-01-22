'use strict';

angular.module('publisherApp')
    .factory('Subscriber', function ($resource) {
        return $resource('api/subscribers/:subject_id/:subject_type/:ctx_id/:ctx_type', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            //'update': { method:'PUT' },
            'queryForCtx': {
                method: 'GET', isArray: true,
                url: 'api/subscribers/:ctx_type/:ctx_id'
            }
        });
    });
