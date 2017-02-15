'use strict';

angular.module('publisherApp')
    .factory('Permission', function ($resource, DateUtils) {
        return $resource('api/permissions/:id', {}, {
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
            'queryForCtx': {
                method: 'GET', isArray: true,
                url:'api/permissions/:ctx_type/:ctx_id'
            }
        });
    });
