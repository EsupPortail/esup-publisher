'use strict';

angular.module('publisherApp')
    .factory('User', function ($resource) {
        return $resource('api/users/:login', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'details':{
                url: 'api/users/extended/:login',
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.createdDate = new Date(data.createdDate);
                    data.lastModifiedDate = new Date(data.lastModifiedDate);
                    return data;
                }
            },
            'attributes':{
                url: 'api/users/attributes',
                method: 'GET', isArray: true
            },
            'funtionalAttributes':{
                url: 'api/users/fnattributes',
                method: 'GET', isArray: true
            },
            'canCreateInCtx': {
                url: 'api/users/perm/createin',
                method: 'GET'
            },
            'canEditCtx': {
                url: 'api/users/perm/edit',
                method: 'GET'
            },
            'canDeleteCtx': {
                url: 'api/users/perm/delete',
                method: 'GET'
            },
            'canEditCtxPerms': {
                url: 'api/users/perm/editPerms',
                method: 'GET'
            },
            'canEditCtxTargets': {
                url: 'api/users/perm/editTargets',
                method: 'GET'
            },
            //'search': {
            //    url: 'api/users/search/:ctx_id/:ctx_type',
            //    method: 'GET', isArray: true,
            //    transformResponse: function (data) {
            //        data = angular.fromJson(data);
            //        return data;
            //    }
            //},
            'search': {
                url: 'api/users/search',
                method: 'POST', isArray: true,
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
