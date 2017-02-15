'use strict';

angular.module('publisherApp')
    .factory('PermissionOnSubjects', function ($resource, DateUtils) {
        return $resource('api/permissionOnSubjectss/:id', {}, {
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
                url:'api/permissionOnSubjectss/:ctx_type/:ctx_id'
            }
        });
    });
