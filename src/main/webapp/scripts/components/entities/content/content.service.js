'use strict';

angular.module('publisherApp')
    .factory('ContentDTO', function ($resource) {
        return $resource('api/contents/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.item.createdDate = new Date(data.item.createdDate);
                    data.item.lastModifiedDate = new Date(data.item.lastModifiedDate);
                    data.item.startDate = new Date(data.item.startDate);
                    data.item.endDate = new Date(data.item.endDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
