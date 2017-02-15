'use strict';

angular.module('publisherApp')
    .factory('PermissionOnContext', function ($resource, DateUtils) {
        return $resource('api/permissionOnContexts/:id', {}, {
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
                url:'api/permissionOnContexts/:ctx_type/:ctx_id'
            }
        });
    });
