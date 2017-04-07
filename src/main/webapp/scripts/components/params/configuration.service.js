'use strict';

angular.module('publisherApp')
    .factory('ConfImageSize', function ($resource) {
        return $resource('api/conf/uploadimagesize/', {}, {
            'query': { method: 'GET' }
        });
    }).factory('ConfFileSize', function ($resource) {
        return $resource('api/conf/uploadfilesize/', {}, {
            'query': { method: 'GET' }
        });
    }).factory('Configuration', function($q, $state, ConfImageSize, ConfFileSize) {
        var confImageSize,confFileSize;
        return {
            init: function () {
                return $q.all([ConfImageSize.query().$promise, ConfFileSize.query().$promise])
                    .then(function (results) {
                        confImageSize = results[0].value;
                        confFileSize = results[1].value;
                    }).catch(function (error) {
                        $state.go("error");
                    });
            },
            getConfUploadImageSize: function () {
                if (!confImageSize) {this.init();}
                return confImageSize;
            },
            getConfUploadFileSize: function () {
                if (!confFileSize) {this.init();}
                return confFileSize;
            }
        }
    });
