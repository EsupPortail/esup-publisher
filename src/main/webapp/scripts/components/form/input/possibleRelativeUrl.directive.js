'use strict';

angular.module('publisherApp')
    .directive('possibleRelativeUrl', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function($scope, elem, attr, ctrl) {
            var URL_REGEXP = /^(((ftp|http)s?:\/\/)|(\/))(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/;
            ctrl.$validators.possibleRelativeUrl = function(modelValue, viewValue) {
                var value = modelValue || viewValue;
                return ctrl.$isEmpty(value) || URL_REGEXP.test(value);
            };
        }
    };
});
