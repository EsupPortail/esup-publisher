'use strict';

angular.module('publisherApp')
    .factory('FileManager', function ($resource) {
        return $resource('api/file/:entityId/:isPublic/:fileUri', {}, {
        });
    });
