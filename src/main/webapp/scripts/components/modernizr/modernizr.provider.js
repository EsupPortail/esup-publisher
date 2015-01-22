'use strict';

angular.module('publisherApp')
    .provider('modernizr', function() {

        this.$get = function () {
            return Modernizr || {};
        };
    });
