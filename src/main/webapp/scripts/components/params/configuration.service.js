'use strict';

angular.module('publisherApp')
    .factory('ConfImageSize', function ($resource) {
        return $resource('api/conf/uploadimagesize/', {}, {
            'query': { method: 'GET' }
        });
    }).factory('Configuration', function($q, $state, ConfImageSize) {
        var confImageSize;
        return {
            init: function () {
                return $q.all([ConfImageSize.query().$promise])
                    .then(function (results) {
                        confImageSize = results[0].value;
                    }).catch(function (error) {
                        $state.go("error");
                    });
            },
            getConfUploadImageSize: function () {
                if (!confImageSize) {this.init();}
                return confImageSize;
            }
        }
    });
