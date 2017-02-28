'use strict';

angular.module('publisherApp')
    .directive('dateGreaterThan', ["$filter", "DateService", function ($filter, DateService) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attrs, ctrl) {
                var validateDateRange = function (inputValue) {
                    var comparison = attrs.dateGreaterThan;
                    var comparisonMin = attrs.minDate;
                    if(!inputValue || !(comparison || comparisonMin)){
                        // It's valid because we have nothing to compare against
                        ctrl.$setValidity('dateLowerThan', true);
                        return inputValue;
                    }
                    var fromDate = DateService.normalize(comparison);
                    var minDate = DateService.normalize(comparisonMin);
                    //console.log("dateGreaterthan with dates : ", fromDate, minDate);
                    if (DateService.getDateDifference(fromDate, minDate) > 0) {
                        fromDate = minDate;
                        //console.log("Comparing with minDate ", minDate);
                    }
                    var toDate = DateService.normalize(inputValue);
                    var isValid = DateService.isValidDateRange(fromDate, toDate);
                    //console.log("dateGreaterthan ", isValid);
                    ctrl.$setValidity('dateGreaterThan', isValid);
                    return inputValue;
                };

                ctrl.$parsers.unshift(validateDateRange);
                ctrl.$formatters.push(validateDateRange);
                attrs.$observe('dateGreaterThan', function () {
                    return validateDateRange(ctrl.$viewValue);

                });
            }
        };
    }]);

