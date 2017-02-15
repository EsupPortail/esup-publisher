'use strict';

angular.module('publisherApp')
    .factory('DateUtils', ['$filter', function ($filter) {
    return {

        convertDateTimeFromServer : function convertDateTimeFromServer(date) {
            if (date) {
                return new Date(date);
            } else {
                return null;
            }
        },

        convertLocalDateFromServer : function convertLocalDateFromServer(date) {
            if (date) {
                var dateString = date.split('-');
                return new Date(dateString[0], dateString[1] - 1, dateString[2]);
            }
            return null;
        },

        convertLocalDateToServer : function convertLocalDateToServer(date) {
            if (date) {
                return $filter('date')(date, 'yyyy-MM-dd');
            } else {
                return null;
            }
        },

        dateformat : function dateformat() {
            return 'yyyy-MM-dd';
        }
    };
}]);
