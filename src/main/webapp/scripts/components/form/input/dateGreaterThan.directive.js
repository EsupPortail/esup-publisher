'use strict';

angular.module('publisherApp')
    .directive('dateGreaterThan', ["$filter", "DateService", function ($filter, DateService) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attrs, ctrl) {
                var validateDateRange = function (inputValue) {
                    var fromDate = DateService.normalize(attrs.dateGreaterThan);
                    var minDate = DateService.normalize(attrs.minDate);
                    console.log("dateGreaterthan with dates : ", fromDate, minDate);
                    if (DateService.getDateDifference(fromDate, minDate) > 0) {
                        fromDate = minDate;
                        console.log("Comparing with minDate ", minDate);
                    }
                    var toDate = DateService.normalize(inputValue);
                    var isValid = DateService.isValidDateRange(fromDate, toDate);
                    console.log("dateGreaterthan ", isValid);
                    ctrl.$setValidity('dateGreaterThan', isValid);
                    return inputValue;
                };

                ctrl.$parsers.unshift(validateDateRange);
                ctrl.$formatters.push(validateDateRange);
                attrs.$observe('dateGreaterThan', function () {
                    validateDateRange(ctrl.$viewValue);

                });
            }
        };
    }]);

