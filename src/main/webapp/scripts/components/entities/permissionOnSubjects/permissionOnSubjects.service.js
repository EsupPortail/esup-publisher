'use strict';

angular.module('publisherApp')
    .factory('PermissionOnSubjects', function ($resource) {
        return $resource('api/permissionOnSubjectss/:id', {}, {
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
            'update': { method:'PUT' },
            'queryForCtx': {
                method: 'GET', isArray: true,
                url:'api/permissionOnSubjectss/:ctx_type/:ctx_id'
            }
        });
    });
