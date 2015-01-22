'use strict';

angular.module('publisherApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


