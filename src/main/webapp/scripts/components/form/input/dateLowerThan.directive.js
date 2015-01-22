'use strict';

angular.module('publisherApp')
    .directive('dateLowerThan', ["$filter", "DateService", function ($filter, DateService) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attrs, ctrl) {
                var validateDateRange = function (inputValue) {
                    var fromDate = DateService.normalize(inputValue);
                    var toDate = DateService.normalize(attrs.dateLowerThan);
                    var isValid = DateService.isValidDateRange(fromDate, toDate);
                    ctrl.$setValidity('dateLowerThan', isValid);
                    return inputValue;
                };

                ctrl.$parsers.unshift(validateDateRange);
                ctrl.$formatters.push(validateDateRange);
                attrs.$observe('dateLowerThan', function () {
                    validateDateRange(ctrl.$viewValue);
                });
            }
        };
    }]);

