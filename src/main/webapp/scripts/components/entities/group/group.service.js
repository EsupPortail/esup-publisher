'use strict';

angular.module('publisherApp')
    .factory('Group', function ($resource) {
        return $resource('api/groups/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'details':{
                url: 'api/groups/extended/:id',
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'attributes':{
                url: 'api/groups/attributes',
                method: 'GET', isArray: true
            },
            'userMembers': {
                url: 'api/groups/usermembers',
                method: 'GET', isArray: true
            }
        });
    });
