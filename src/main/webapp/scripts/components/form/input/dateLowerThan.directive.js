'use strict';

angular.module('publisherApp')
    .directive('dateLowerThan', ["$filter", "DateService", function ($filter, DateService) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attrs, ctrl) {
                var validateDateRange = function (inputValue) {
                    var comparisonModel = attrs.dateLowerThan;
                    if(!inputValue || !comparisonModel){
                        // It's valid because we have nothing to compare against
                        ctrl.$setValidity('dateLowerThan', true);
                        return inputValue;
                    }
                    var fromDate = DateService.normalize(inputValue);
                    var toDate = DateService.normalize(comparisonModel);
                    var isValid = DateService.isValidDateRange(fromDate, toDate);
                    ctrl.$setValidity('dateLowerThan', isValid);
                    return inputValue;
                };

                ctrl.$parsers.unshift(validateDateRange);
                ctrl.$formatters.push(validateDateRange);
                attrs.$observe('dateLowerThan', function () {
                    return validateDateRange(ctrl.$viewValue);
                });
            }
        };
    }]);

